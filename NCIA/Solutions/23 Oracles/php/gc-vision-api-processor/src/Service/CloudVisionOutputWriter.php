<?php


namespace App\Service;


use App\Constant\ImageFolderName;
use App\Constant\JsonOutputFilePrefix;
use App\Entity\ImageMetaData;
use App\Repository\ImageMetaDataRepository;
use Doctrine\ORM\EntityManagerInterface;

class CloudVisionOutputWriter
{
    /** @var EntityManagerInterface */
    private $em;
    /** @var SettingsContainer */
    private $settings;

    public function __construct(EntityManagerInterface $em,
                                SettingsContainer $settings)
    {
        $this->em = $em;
        $this->settings = $settings;
    }

    public function writeAll()
    {
        /** @var ImageMetaData[] $images */
        $images = $this->imageMetaDataRepository()->findAll();
        foreach ($images as $image) {
            $this->writeJsonFile($image);
        }
    }


    /**
     * @param ImageMetaData $image
     * @throws \Exception
     */
    private function writeJsonFile(ImageMetaData $image)
    {
        $jsonBody = self::preformatJsonBody($image->getCloudVisionOutput());

        switch ($image->getFolderName()) {
            case ImageFolderName::BAD: $dir = $this->settings->getOutputBadImagesDir(); break;
            case ImageFolderName::NEUTRAL_GOOD: $dir = $this->settings->getOutputNeutralGoodImagesDir(); break;
            case ImageFolderName::BENCHMARK: $dir = $this->settings->getOutputBenchmarkImagesDir(); break;
            default: throw new \Exception('Invalid folderName: '.$image->getFolderName());
        }

        $outputFilename = $dir.self::getJsonOutputFilename($image);

        file_put_contents($outputFilename, $this->prettyPrintJson($jsonBody));
    }


    /**
     * @param string $json
     * @return string
     */
    public static function preformatJsonBody(string $json): string
    {
        return strtr($json, ['\/' => '/']);
    }


    /**
     * @param string $json
     * @return string
     */
    private function prettyPrintJson(string $json): string
    {
        $array = json_decode($json, true);
        return self::preformatJsonBody(json_encode($array, JSON_PRETTY_PRINT));
    }


    /**
     * @param ImageMetaData $image
     * @return string
     * @throws \Exception
     */
    public static function getJsonOutputFilename(ImageMetaData $image)
    {
        switch ($image->getFolderName()) {
            case ImageFolderName::BAD: $prefix = JsonOutputFilePrefix::BAD; break;
            case ImageFolderName::NEUTRAL_GOOD: $prefix = JsonOutputFilePrefix::NEUTRAL_GOOD; break;
            case ImageFolderName::BENCHMARK: $prefix = JsonOutputFilePrefix::BENCHMARK; break;
            default: throw new \Exception('invalid foldername:'. $image->getFolderName());
        }
        return $prefix . self::replaceImageExtensionWithJson($image->getFilename());
    }


    /**
     * @param string $filename
     * @return string
     */
    private static function replaceImageExtensionWithJson(string $filename): string
    {
        $jsonExtension = '.json';

        return strtr($filename, [
            '.jpg' => $jsonExtension,
            '.jpeg' => $jsonExtension,
            '.png' => $jsonExtension,
            '.gif' => $jsonExtension,
            '.bmp' => $jsonExtension,
        ]);
    }


    /**
     * @return ImageMetaDataRepository
     */
    private function imageMetaDataRepository(): ImageMetaDataRepository
    {
        return $this->em->getRepository(ImageMetaData::class);
    }

}