<?php


namespace App\Service;


use GuzzleHttp\Client;
use Symfony\Component\HttpFoundation\Request;

class GoogleCloudVisionService
{
    /** @var SettingsContainer */
    private $settings;

    public function __construct(SettingsContainer $settings)
    {
        $this->settings = $settings;
    }

    /**
     * @param string $base64EncodedImageContent
     * @return string Return the response as a json string similar to format shown here https://cloud.google.com/vision/
     *
     * @throws \GuzzleHttp\Exception\GuzzleException
     */
    public function postSingleImagesAnnotateRequest(string $base64EncodedImageContent)
    {
        $jsonResponseBody = $this->postImagesAnnotateRequest([$base64EncodedImageContent]);
        $responseAsArray = json_decode($jsonResponseBody,true);
        $jsonResponseWithoutResponsesKey = array_shift($responseAsArray['responses']);
        return CloudVisionOutputWriter::preformatJsonBody(json_encode($jsonResponseWithoutResponsesKey));
    }


    /**
     * @param array $base64EncodedImagesContents
     * @return mixed|\Psr\Http\Message\ResponseInterface
     * @throws \GuzzleHttp\Exception\GuzzleException
     */
    public function postImagesAnnotateRequest(array $base64EncodedImagesContents = [])
    {
        $client = new Client();
        $res = $client->request(
            Request::METHOD_POST,
            $this->getImagesAnnotateUri(),
            [
                'body' => $this->getImagesAnnotateBody($base64EncodedImagesContents),
                'headers' => [
                        'Accept' => 'application/json',
                        'Content-type' => 'application/json'
                    ]
            ]
        );
        $client = null;
        return $res->getBody()->getContents();
    }


    /**
     * @return string
     */
    private function getImagesAnnotateUri(): string
    {
        return 'https://vision.googleapis.com/v1/images:annotate'
            .'?alt=json'
            .'&key='.$this->settings->getGoogleCloudApiKey()
            ;
    }


    /**
     * @param array $imagesContents
     * @return string
     */
    private function getImagesAnnotateBody(array $imagesContents = []): string
    {
        $requests = [];
        foreach ($imagesContents as $imagesContent) {
            $requests[] = [
                "image" => [
                    "content" => $imagesContent,
                ],
                "features" => $this->getImagesAnnotationFeaturesAsArray()
            ];
        }

        $body = [
            'requests' => $requests
        ];

        return json_encode($body);
    }


    private function getImagesAnnotationFeaturesAsArray(): array
    {
        $features = [];
        $types = [
            'TYPE_UNSPECIFIED',
            'FACE_DETECTION',
            'LANDMARK_DETECTION',
            'LOGO_DETECTION',
            'LABEL_DETECTION',
            'TEXT_DETECTION',
            'SAFE_SEARCH_DETECTION',
            'IMAGE_PROPERTIES',
            'CROP_HINTS',
            'WEB_DETECTION',
            'OBJECT_LOCALIZATION',
        ];

        foreach ($types as $type)
        {
            $features[] = [
                'maxResults' => 100,
                'type' => $type
            ];
        }

        return $features;
    }
}