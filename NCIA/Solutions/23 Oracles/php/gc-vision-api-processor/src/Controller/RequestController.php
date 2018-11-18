<?php

namespace App\Controller;

use App\Constant\ImageFolderName;
use App\Entity\ImageMetaData;
use App\Repository\ImageMetaDataRepository;
use App\Service\GoogleCloudVisionService;
use App\Service\PropagandaAnalyzer;
use Doctrine\ORM\EntityManager;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\DependencyInjection\ContainerInterface;
use Symfony\Component\HttpFoundation\File\File;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpKernel\Exception\BadRequestHttpException;
use Symfony\Component\HttpKernel\Exception\PreconditionFailedHttpException;
use Symfony\Component\Routing\Annotation\Route;

class RequestController extends AbstractController
{
		/**
		 * @Route("/", name="index", methods={"GET"})
		 * @param GoogleCloudVisionService $googleCloudVisionService
		 * @return \Symfony\Component\HttpFoundation\Response
		 */
    public function index(GoogleCloudVisionService $googleCloudVisionService)
    {
        return $this->render('request/index.html.twig', []);
    }

		/**
		 * @Route("/uploadImage", name="uploadImage", methods={"POST"})
		 * @param Request $request
		 * @param ContainerInterface $container
		 * @param PropagandaAnalyzer $propagandaAnalyzer
		 * @return JsonResponse
		 */
    public function imageUploadHandler(Request $request, ContainerInterface $container, PropagandaAnalyzer $propagandaAnalyzer)
    {
		    $output = array('uploaded' => false);
		    // get the file from the request object
	      /** @var File $file */
		    $file = $request->files->get('file');
		    // generate a new filename (safer, better approach)
		    // To use original filename, $fileName = $this->file->getClientOriginalName();
		    $fileName = $file->getClientOriginalName();

		    // set your uploads directory

		    $uploadDir = $container->getParameter('kernel.project_dir') . '/public/';
		    if (!file_exists($uploadDir) && !is_dir($uploadDir)) {
			    mkdir($uploadDir, 0775, true);
		    }
		    if ($file->move($uploadDir, $fileName)) {
			    $output['uploaded'] = true;
			    $output['fileName'] = $fileName;
		    }

		    if ($output['uploaded']) {
		    	$encodedImg = base64_encode(file_get_contents($uploadDir.$fileName));
		    	try {
                    $propagandaAnalyzer->processBase64EncodedImage($fileName, $encodedImg);
                } catch (PreconditionFailedHttpException $exception) {
                    throw new BadRequestHttpException($exception->getMessage());
                }
		    }

		    return new JsonResponse($output);
    }

	/**
	 * @Route("/overview/{filename}", name="overview", methods={"GET"})
	 * @param $filename
	 * @param PropagandaAnalyzer $propagandaAnalyzer
	 * @return \Symfony\Component\HttpFoundation\Response
	 */
		public function overview($filename, PropagandaAnalyzer $propagandaAnalyzer)
		{
			  $output = $propagandaAnalyzer->checkPropagandaAnalyzerResult($filename);

				return $this->render('request/overview.html.twig', [
					'filename' => $filename,
					'output' => $output,
				]);
		}

	/**
	 * @Route("/analysis/{filename}", name="analysis", methods={"GET"})
	 * @param $filename
	 * @param GoogleCloudVisionService $googleCloudVisionService
	 * @param EntityManagerInterface $entityManager
	 * @return \Symfony\Component\HttpFoundation\Response
	 */
		public function analysis($filename, PropagandaAnalyzer $propagandaAnalyzer)
		{
				$output = $propagandaAnalyzer->checkPropagandaAnalyzerResult($filename);

				return $this->render('request/analysis.html.twig', [
					'filename' => $filename,
					'output' => $output
				]);
		}
}
