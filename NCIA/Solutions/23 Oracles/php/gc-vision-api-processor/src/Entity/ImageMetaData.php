<?php


namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;

/**
 * Class ImageMetaData
 *
 * @ORM\Entity(repositoryClass="App\Repository\ImageMetaDataRepository")
 * @package App\Entity
 */
class ImageMetaData
{
    /**
     * @var integer
     *
     * @ORM\Id
     * @ORM\Column(type="integer")
     * @ORM\GeneratedValue(strategy="IDENTITY")
     */
    private $id;

    /**
     * @var \DateTime
     * @ORM\Column(type="datetime", options={"default":"CURRENT_TIMESTAMP"}, nullable=true)
     * @Assert\Date
     * @Assert\NotBlank
     */
    private $logDate;


    /**
     * @var string
     * @ORM\Column(type="string", nullable=false)
     * @Assert\NotBlank
     */
    private $filename;


    /**
     * @var string
     * @ORM\Column(type="string", nullable=false)
     * @Assert\NotBlank
     */
    private $folderName;


    /**
     * @var boolean|null
     * @ORM\Column(type="boolean", nullable=true)
     */
    private $isLabelledBadPropaganda;


    /**
     * @var boolean|null
     * @ORM\Column(type="boolean", nullable=true)
     */
    private $isDetectedAsBadPropaganda;


    /**
     * @var float
     * @ORM\Column(type="float", options={"default":0})
     */
    private $detectionConfidence;


    /**
     * @var string|null
     * @ORM\Column(type="text", nullable=true)
     */
    private $cloudVisionOutput;

    /**
     * @var string
     * @ORM\Column(type="string", nullable=true)
     */
    private $propagandaAnalysisId;

    /**
     * @var string|null
     * @ORM\Column(type="text", nullable=true)
     */
    private $propagandaAnalysisOutput;

    /**
     * ImageMetaData constructor.
     */
    public function __construct()
    {
        $this->logDate = new \DateTime();
        $this->detectionConfidence = 0;
    }

    /**
     * @return int
     */
    public function getId(): int
    {
        return $this->id;
    }

    /**
     * @return \DateTime
     */
    public function getLogDate(): \DateTime
    {
        return $this->logDate;
    }

    /**
     * @param \DateTime $logDate
     * @return ImageMetaData
     */
    public function setLogDate(\DateTime $logDate): ImageMetaData
    {
        $this->logDate = $logDate;
        return $this;
    }

    /**
     * @return string
     */
    public function getFilename(): string
    {
        return $this->filename;
    }

    /**
     * @param string $filename
     * @return ImageMetaData
     */
    public function setFilename(string $filename): ImageMetaData
    {
        $this->filename = $filename;
        return $this;
    }

    /**
     * @return string
     */
    public function getFolderName(): string
    {
        return $this->folderName;
    }

    /**
     * @param string $folderName
     * @return ImageMetaData
     */
    public function setFolderName(string $folderName): ImageMetaData
    {
        $this->folderName = $folderName;
        return $this;
    }

    /**
     * @return bool|null
     */
    public function isLabelledBadPropaganda(): ?bool
    {
        return $this->isLabelledBadPropaganda;
    }

    /**
     * @param bool|null $isLabelledBadPropaganda
     * @return ImageMetaData
     */
    public function setIsLabelledBadPropaganda(?bool $isLabelledBadPropaganda): ImageMetaData
    {
        $this->isLabelledBadPropaganda = $isLabelledBadPropaganda;
        return $this;
    }

    /**
     * @return bool|null
     */
    public function isDetectedAsBadPropaganda(): ?bool
    {
        return $this->isDetectedAsBadPropaganda;
    }

    /**
     * @param bool|null $isDetectedAsBadPropaganda
     * @return ImageMetaData
     */
    public function setIsDetectedAsBadPropaganda(?bool $isDetectedAsBadPropaganda): ImageMetaData
    {
        $this->isDetectedAsBadPropaganda = $isDetectedAsBadPropaganda;
        return $this;
    }

    /**
     * @return float
     */
    public function getDetectionConfidence(): float
    {
        return $this->detectionConfidence;
    }

    /**
     * @param float $detectionConfidence
     * @return ImageMetaData
     */
    public function setDetectionConfidence(float $detectionConfidence): ImageMetaData
    {
        $this->detectionConfidence = $detectionConfidence;
        return $this;
    }

    /**
     * @return null|string
     */
    public function getCloudVisionOutput(): ?string
    {
        return $this->cloudVisionOutput;
    }

    /**
     * @param null|string $cloudVisionOutput
     * @return ImageMetaData
     */
    public function setCloudVisionOutput(?string $cloudVisionOutput): ImageMetaData
    {
        $this->cloudVisionOutput = $cloudVisionOutput;
        return $this;
    }

    /**
     * @return string
     */
    public function getPropagandaAnalysisId(): ?string
    {
        return $this->propagandaAnalysisId;
    }

    /**
     * @param string $propagandaAnalysisId
     * @return ImageMetaData
     */
    public function setPropagandaAnalysisId(string $propagandaAnalysisId): ImageMetaData
    {
        $this->propagandaAnalysisId = $propagandaAnalysisId;
        return $this;
    }


    /**
     * @return bool
     */
    public function hasPropagandaAnalysisOutput(): bool
    {
        return !empty($this->getPropagandaAnalysisOutput());
    }


    /**
     * @return null|string
     */
    public function getPropagandaAnalysisOutput(): ?string
    {
        return $this->propagandaAnalysisOutput;
    }

    /**
     * @param null|string $propagandaAnalysisOutput
     * @return ImageMetaData
     */
    public function setPropagandaAnalysisOutput(?string $propagandaAnalysisOutput): ImageMetaData
    {
        $this->propagandaAnalysisOutput = $propagandaAnalysisOutput;
        return $this;
    }



}