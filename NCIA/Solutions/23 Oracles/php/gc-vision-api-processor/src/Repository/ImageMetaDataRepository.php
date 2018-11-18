<?php


namespace App\Repository;
use App\Entity\ImageMetaData;

/**
 * Class ImageMetaDataRepository
 * @package App\Repository
 */
class ImageMetaDataRepository extends BaseRepository
{
    /**
     * @param string $folderName
     * @param string $filename
     * @return ImageMetaData|null
     */
    public function findOneByFileData($folderName, $filename): ?ImageMetaData
    {
        return $this->findOneBy([
            'folderName' => $folderName,
            'filename' => $filename,
        ]);
    }

}