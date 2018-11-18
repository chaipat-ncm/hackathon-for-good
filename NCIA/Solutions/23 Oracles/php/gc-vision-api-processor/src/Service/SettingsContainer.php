<?php


namespace App\Service;


use App\Constant\ImageFolderName;

class SettingsContainer
{

    public function getEnv(): string
    {
        return empty(getenv('APP_ENV')) ? 'dev' : getenv('APP_ENV');
    }

    public function getAppSecret(): string
    {
        return getenv('APP_SECRET');
    }

    public function getProjectRootDir(): string
    {
        return rtrim(getenv('PROJECT_ROOT_DIR'),'/').'/';
    }

    public function getResourcesDir(): string
    {
        return $this->getProjectRootDir() . 'resources/';
    }

    public function getImagesDir(): string
    {
        return $this->getResourcesDir().'images/';
    }

    public function getBadImagesDir(): string
    {
        return $this->getImagesDir().ImageFolderName::BAD.'/';
    }

    public function getBenchmarkImagesDir(): string
    {
        return $this->getImagesDir().ImageFolderName::BENCHMARK.'/';
    }

    public function getNeutralGoodImagesDir(): string
    {
        return $this->getImagesDir().ImageFolderName::NEUTRAL_GOOD.'/';
    }

    public function getOutputDir(): string
    {
        return $this->getResourcesDir().'output/';
    }

    public function getOutputBadImagesDir(): string
    {
        return $this->getOutputDir().ImageFolderName::BAD.'/';
    }

    public function getOutputBenchmarkImagesDir(): string
    {
        return $this->getOutputDir().ImageFolderName::BENCHMARK.'/';
    }

    public function getOutputNeutralGoodImagesDir(): string
    {
        return $this->getOutputDir().ImageFolderName::NEUTRAL_GOOD.'/';
    }

    public function getGoogleCloudApiKey(): string
    {
        return getenv('GOOGLE_CLOUD_API_KEY');
    }

    public function getPropagandaAnalyzerPostUri(): string
    {
        return getenv('PROPAGANDA_ANALYZER_BASE_URI').
            rtrim(getenv('PROPAGANDA_ANALYZER_POST_ENDPOINT').'/')
            ;
    }

    /**
     * @param string $propagandaAnalyzerId
     * @return string
     */
    public function getPropagandaAnalyzerResultsUri(string $propagandaAnalyzerId): string
    {
        return getenv('PROPAGANDA_ANALYZER_BASE_URI').
            rtrim(getenv('PROPAGANDA_ANALYZER_GET_RESULTS_ENDPOINT').'/').'?'.
            $propagandaAnalyzerId
            ;
    }


    /**
     * @return bool
     */
    public function useTestData(): bool
    {
        $value = getenv('USE_TEST_DATA');
        return is_bool($value) && $value ||
            strtolower($value) === 'true';
    }
}