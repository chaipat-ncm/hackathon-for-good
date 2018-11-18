<?php

namespace App\Command;

use App\Constant\CommandOption;
use App\Constant\CommandOptionShortCut;
use App\Service\CloudVisionAnalyzer;
use App\Service\CloudVisionOutputWriter;
use App\Service\TestData;
use Symfony\Component\Console\Command\Command;
use Symfony\Component\Console\Input\InputInterface;
use Symfony\Component\Console\Input\InputOption;
use Symfony\Component\Console\Output\OutputInterface;
use Symfony\Component\Console\Style\SymfonyStyle;

class CloudVisionCommand extends Command
{
    const DESCRIPTION = 'Step 1 of workflow: Retrieve and store cloud vision results of images';

    protected static $defaultName = 'app:cloud-vision';

    /** @var CloudVisionAnalyzer */
    private $analyzer;
    /** @var CloudVisionOutputWriter */
    private $writer;

    public function __construct(CloudVisionAnalyzer $analyzer,
                                CloudVisionOutputWriter $writer)
    {
        $this->analyzer = $analyzer;
        $this->writer = $writer;
        parent::__construct();
    }


    protected function configure()
    {
        $this
            ->setDescription(self::DESCRIPTION)
            ->addOption(CommandOption::OPTION, CommandOptionShortCut::OPTION,
                InputOption::VALUE_OPTIONAL, 'Option description')
        ;
    }

    protected function execute(InputInterface $input, OutputInterface $output)
    {
        $io = new SymfonyStyle($input, $output);

        $io->title('CLOUD VISION ANALYZER');
        $io->writeln(self::DESCRIPTION);

        $option = $input->getOption(CommandOption::OPTION);
        $option = !empty($option) && is_string($option) ? strtolower($option) : null;

        switch ($option) {
            case 'bad':
                $io->comment('analyzing bad images');
                $this->analyzer->analyzeBadImages();
                break;
            case 'neutral-good':
                $io->comment('analyzing neutral-good images');
                $this->analyzer->analyzeNeutralGoodImages();
                break;
            case 'benchmark':
                $io->comment('analyzing benchmark images');
                $this->analyzer->analyzeBenchmarkImages();
                break;
            default:
                $io->comment('analyzing all images');
                $this->analyzer->analyzeAllImages();
                break;
        }

        $io->success('Analyzed images!');

        $this->writer->writeAll();

        $io->success('Wrote all analyzed images!');

        $io->success('Done!');
    }
}
