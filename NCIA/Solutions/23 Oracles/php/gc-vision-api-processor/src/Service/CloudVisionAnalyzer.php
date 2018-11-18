<?php


namespace App\Service;


use App\Constant\ImageFolderName;
use App\Entity\ImageMetaData;
use App\Repository\ImageMetaDataRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Component\HttpKernel\Exception\BadRequestHttpException;
use Symfony\Component\HttpKernel\Exception\PreconditionFailedHttpException;

class CloudVisionAnalyzer
{
    /** @var EntityManagerInterface */
    private $em;
    /** @var SettingsContainer */
    private $settings;
    /** @var GoogleCloudVisionService */
    private $cloudVision;

    public function __construct(EntityManagerInterface $manager,
                                SettingsContainer $settings,
                                GoogleCloudVisionService $cloudVision)
    {
        $this->em = $manager;
        $this->settings = $settings;
        $this->cloudVision = $cloudVision;
    }

    public function analyzeAllImages()
    {
        $this->analyzeBadImages();
        $this->analyzeNeutralGoodImages();
        $this->analyzeBenchmarkImages();
    }

    public function analyzeBadImages()
    {
        dump($this->settings->useTestData());die;
        $files = scandir($this->settings->getBadImagesDir());
        foreach ($files as $filename) {
            $this->analyzeImage($filename, ImageFolderName::BAD);
        }
    }

    public function analyzeNeutralGoodImages()
    {
        $files = scandir($this->settings->getNeutralGoodImagesDir());
        foreach ($files as $filename) {
            $this->analyzeImage($filename, ImageFolderName::NEUTRAL_GOOD);
        }
    }

    public function analyzeBenchmarkImages()
    {
        $files = scandir($this->settings->getBenchmarkImagesDir());
        foreach ($files as $filename) {
            $this->analyzeImage($filename, ImageFolderName::BENCHMARK);
        }
    }


    /**
     * @param string $filename
     * @param string $folderName
     * @throws \GuzzleHttp\Exception\GuzzleException
     */
    private function analyzeImage(string $filename, string $folderName)
    {
        // Ignore empty filename and filenames only containing dots
        $filenameValidationValue = trim($filename,'.');
        if (empty($filenameValidationValue)) {
            return;
        }

        $imageMetaData = $this->imageMetaDataRepository()->findOneByFileData($folderName, $filename);

        if (self::isHit($imageMetaData, true)) {
            return;
        }

        switch ($folderName) {
            case ImageFolderName::BAD:
                $dirPath = $this->settings->getBadImagesDir();
                break;
            case ImageFolderName::BENCHMARK:
                $dirPath = $this->settings->getBenchmarkImagesDir();
                break;
            case ImageFolderName::NEUTRAL_GOOD:
                $dirPath = $this->settings->getNeutralGoodImagesDir();
                break;
            default: throw new PreconditionFailedHttpException('Invalid folderName: '.$folderName);
        }

        # the name of the image file to annotate
        $fileNameWithFullPath = $dirPath . $filename;

        # prepare the image to be annotated
        $image = file_get_contents($fileNameWithFullPath);

        $imageContent = base64_encode($image);

        try {
            $jsonEncodedResponse = $this->cloudVision->postSingleImagesAnnotateRequest($imageContent);
        } catch (\GuzzleHttp\Exception\GuzzleException $exception) {
            dump([
                'filename' => $filename,
                'foldername' => $folderName
            ]);
            throw $exception;
        }

        $imageMetaData = (new ImageMetaData())
            ->setFilename($filename)
            ->setFolderName($folderName)
            ->setCloudVisionOutput($jsonEncodedResponse)
            ->setIsLabelledBadPropaganda($this->getIsLabelledBadPropagandaFromFolderName($folderName))
        ;

        $this->em->persist($imageMetaData);
        $this->em->flush();
    }


    private function getIsLabelledBadPropagandaFromFolderName(string $folderName): bool
    {
        return strtolower(ImageFolderName::BAD) === strtolower($folderName);
    }


    /**
     * @param ImageMetaData|null $imageMetaData
     * @param bool $mustHaveVisionResponse
     * @return bool
     */
    public static function isHit(?ImageMetaData $imageMetaData, bool $mustHaveVisionResponse)
    {
        if ($imageMetaData) {
            return $mustHaveVisionResponse ? !empty($imageMetaData->getCloudVisionOutput()) : true;
        }

        return false;
    }


    /**
     * @return ImageMetaDataRepository
     */
    private function imageMetaDataRepository(): ImageMetaDataRepository
    {
        return $this->em->getRepository(ImageMetaData::class);
    }



    public function analyzeBase64EncodedImageBinary(?string $imageContent)
    {
        if (empty($imageContent)) {
            throw new BadRequestHttpException('Image is empty');
        }

        return $this->cloudVision->postSingleImagesAnnotateRequest($imageContent);
    }
}