<?php


namespace App\Service;


use App\Constant\ImageFolderName;
use App\Entity\ImageMetaData;
use App\Repository\ImageMetaDataRepository;
use Doctrine\ORM\EntityManagerInterface;
use GuzzleHttp\Client;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpKernel\Exception\PreconditionFailedHttpException;

class PropagandaAnalyzer
{
    const FOLDER_NAME = ImageFolderName::BENCHMARK;

    /** @var GoogleCloudVisionService */
    private $visionService;
    /** @var EntityManagerInterface */
    private $em;
    /** @var SettingsContainer */
    private $settings;

    public function __construct(EntityManagerInterface $em,
                                GoogleCloudVisionService $cloudVisionService,
                                SettingsContainer $settings)
    {
        $this->em = $em;
        $this->visionService = $cloudVisionService;
        $this->settings = $settings;
    }

    public function processBase64EncodedImage(string $filename, string $base64EncodedImage)
    {
        $imageMetaData = $this->findImage($filename);

        if (!CloudVisionAnalyzer::isHit($imageMetaData, true)) {
            $jsonEncodedResponse = $this->visionService->postSingleImagesAnnotateRequest($base64EncodedImage);
            if ($jsonEncodedResponse === '{"error":{"code":3,"message":"Bad image data."}}') {
                throw new PreconditionFailedHttpException("Bad image data.");
            }

            $this->saveUploadedFileLocally($filename, $base64EncodedImage);

            $imageMetaData = (new ImageMetaData())
                ->setFilename($filename)
                ->setFolderName(self::FOLDER_NAME)
                ->setCloudVisionOutput($jsonEncodedResponse)
                ->setIsLabelledBadPropaganda(false)
            ;

            $this->saveVisionResponseLocally($imageMetaData);

            $this->em->persist($imageMetaData);
            $this->em->flush();

            $this->sendGoogleCloudVisionJsonResponseToPropagandaAnalyzer($imageMetaData);

        } else {
            $jsonEncodedResponse = $imageMetaData->getCloudVisionOutput();
        }

        return new JsonResponse($jsonEncodedResponse,Response::HTTP_OK);
    }


    /**
     * @param string $filename
     * @param string $base64EncodedImage
     */
    private function saveUploadedFileLocally(string $filename, string $base64EncodedImage)
    {
        $filenameWithFullPath = $this->settings->getBenchmarkImagesDir().$filename;
        file_put_contents($filenameWithFullPath, base64_decode($base64EncodedImage));
    }


    /**
     * @param ImageMetaData $imageMetaData
     * @throws \Exception
     */
    private function saveVisionResponseLocally(ImageMetaData $imageMetaData)
    {
        $filenameWithFullPath = $this->settings->getBenchmarkImagesDir().
            CloudVisionOutputWriter::getJsonOutputFilename($imageMetaData);
        file_put_contents($filenameWithFullPath, $imageMetaData->getCloudVisionOutput());
    }


    /**
     * @param ImageMetaData $imageMetaData
     * @throws \GuzzleHttp\Exception\GuzzleException
     */
    private function sendGoogleCloudVisionJsonResponseToPropagandaAnalyzer(ImageMetaData $imageMetaData)
    {
        if ($this->settings->useTestData()) {
            $imageMetaData->setPropagandaAnalysisId($imageMetaData->getFilename());
            $this->em->persist($imageMetaData);
            $this->em->flush();
            return;
        }

        $client = new Client();
        $res = $client->request(
            Request::METHOD_POST,
            $this->getPropagandaAnalyzerUri(),
            [
                'form_params' => [
                    'vision' => $imageMetaData->getCloudVisionOutput(),
                    'filename' => $imageMetaData->getFilename()
                ],
                'headers' => [
                    'Accept' => 'application/json',
                    'Content-type' => 'application/json'
                ]
            ]
        );
        $client = null;

        $propagandaAnalysisId = $imageMetaData->getFilename();

        $imageMetaData->setPropagandaAnalysisId($propagandaAnalysisId);
        $this->em->persist($imageMetaData);
        $this->em->flush();
    }


    /**
     * @param string $filename
     * @return array
     * @throws \GuzzleHttp\Exception\GuzzleException
     */
    public function checkPropagandaAnalyzerResult(string $filename)
    {
        $imageMetaData = $this->findImage($filename);
        if (empty($imageMetaData) || empty($imageMetaData->getPropagandaAnalysisId())) {
            return self::resultOutput('', false);
        }

        $propagandaAnalyzerId = $imageMetaData->getPropagandaAnalysisId();

        if ($this->settings->useTestData()) {
            $body = TestData::propagandaAnalyzerResults($imageMetaData);
            return $body;
        }


        if ($imageMetaData->hasPropagandaAnalysisOutput()) {
            $jsonBody = $imageMetaData->getPropagandaAnalysisOutput();

        } else {
            $client = new Client();
            $res = $client->request(
                Request::METHOD_GET,
                $this->settings->getPropagandaAnalyzerResultsUri($propagandaAnalyzerId),
                [
                    'headers' => [
                        'Accept' => 'application/json',
                        'Content-type' => 'application/json'
                    ]
                ]
            );
            $client = null;
            $jsonBody = CloudVisionOutputWriter::preformatJsonBody($res->getBody()->getContents());
        }

        $content = json_decode($jsonBody,true);
        $isProcessed = ArrayUtil::getBoolean('processed', $content);

        if ($isProcessed) {

            if (!$imageMetaData->hasPropagandaAnalysisOutput()) {
                $imageMetaData->setPropagandaAnalysisOutput($jsonBody);
                $imageMetaData->setDetectionConfidence(
                    ArrayUtil::get('propagandaConfidence', $content)
                );
                $imageMetaData->setIsDetectedAsBadPropaganda(
                    ArrayUtil::getBoolean('isPropaganda', $content)
                );
                $this->em->persist($imageMetaData);
                $this->em->flush();
            }

            $semanticFingerPrint =  ArrayUtil::get('semanticMapImage', $content);

            $categories = ArrayUtil::get('categories', $content, []);
            $belonging = self::getCategoryConfidenceFromAnalysisOutput('belonging', $categories);
            $war = self::getCategoryConfidenceFromAnalysisOutput('war', $categories);
            $brutality = self::getCategoryConfidenceFromAnalysisOutput('brutality', $categories);
            $mercy = self::getCategoryConfidenceFromAnalysisOutput('mercy', $categories);
            $victimhood = self::getCategoryConfidenceFromAnalysisOutput('victimhood', $categories);

            $body = self::resultOutput(
                $imageMetaData->getPropagandaAnalysisId(),
                $isProcessed,
                $imageMetaData->getDetectionConfidence(),
                $imageMetaData->isDetectedAsBadPropaganda(),
                ArrayUtil::get('badPropaganda', $content, 0),
                ArrayUtil::get('neutralPropaganda', $content, 0),
                $belonging,
                $war,
                $brutality,
                $mercy,
                $victimhood,
                ArrayUtil::get('keywords', $content, []),
                ArrayUtil::get('urls', $content, []),
                $imageMetaData->getCloudVisionOutput(),
                $semanticFingerPrint
            );

        } else {
            $body = self::resultOutput($imageMetaData->getPropagandaAnalysisId(), false);
        }

        return $body;
    }


    /**
     * @param string $categoryKey
     * @param array $content
     * @return mixed|null
     */
    public static function getCategoryConfidenceFromAnalysisOutput(string $categoryKey, array $content)
    {
        $categories = ArrayUtil::get('categories', $content, []);
        $categoryValues = ArrayUtil::get($categoryKey, $categories, []);
        return  ArrayUtil::get('confidence', $categoryValues, 0.0);
    }


    /**
     * @param null|string $propagandaAnalysisId
     * @param bool $isProcessed
     * @param float $propagandaConfidence
     * @param bool|null $isPropaganda
     * @param float $badPropaganda
     * @param float $neutralPropaganda
     * @param float $belonging
     * @param float $war
     * @param float $brutality
     * @param float $mercy
     * @param float $victimhood
     * @param array $keywords
     * @param array $urls
     * @param null|string $visionJson
     * @param null|string $semanticFingerPrintBase64EncodedImage
     * @return array
     */
    public static function resultOutput(?string $propagandaAnalysisId,
                                        bool $isProcessed = false,
                                  float $propagandaConfidence = 0,
                                  ?bool $isPropaganda = false,
                                  float $badPropaganda = 0,
                                  float $neutralPropaganda = 0,
                                  float $belonging = 0,
                                  float $war = 0,
                                  float $brutality = 0,
                                  float $mercy = 0,
                                  float $victimhood = 0,
                                  array $keywords = [],
                                  array $urls = [],
                                  ?string $visionJson = '',
                                  ?string $semanticFingerPrintBase64EncodedImage = ''
    )
    {
        return [
            'id' => $propagandaAnalysisId,
            'processed' => $isProcessed,
            'propagandaConfidence' => $propagandaConfidence,
            'isPropaganda' => $isPropaganda,
            'badPropaganda' => $badPropaganda,
            'neutralPropaganda' => $neutralPropaganda,
            'categories' => [
                'belonging' => [
                    'confidence' => $belonging,
                ],
                'war' => [
                    'confidence' => $war,
                ],
                'brutality' => [
                    'confidence' => $brutality,
                ],
                'mercy' => [
                    'confidence' => $mercy,
                ],
                'victimhood' => [
                    'confidence' => $victimhood,
                ],
            ],
            'keywords' => $keywords,
            'urls' => $urls,
            'visionJson' => $visionJson,
            'semanticMapImage' => $semanticFingerPrintBase64EncodedImage,
        ];
    }


    /**
     * @return string
     */
    private function getPropagandaAnalyzerUri(): string
    {
        return $this->settings->getPropagandaAnalyzerPostUri();
    }


    /**
     * @param string $filename
     * @return ImageMetaData|null
     */
    private function findImage(string $filename): ?ImageMetaData
    {
        return $this->imageMetaDataRepository()->findOneByFileData(self::FOLDER_NAME, $filename);
    }


    /**
     * @return ImageMetaDataRepository
     */
    private function imageMetaDataRepository(): ImageMetaDataRepository
    {
        return $this->em->getRepository(ImageMetaData::class);
    }

}