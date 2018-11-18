from django.shortcuts import render

from ncia.forms import ImageUploaderForm
from ncia.models import ImageUploader
from ncia.cloud_vision import get_labels, get_web_entities


def home(request):
    if request.method == 'POST':
        form = ImageUploaderForm(request.POST)
        image = request.POST.getlist('image')
        if form.is_valid():
            for f in image:
                image_instance = ImageUploader(image=f)
                image_instance.save()
    else:
        form = ImageUploaderForm()
    return render(request, 'home.html', {'form':form})

def process(request):
	labels = get_labels(request.FILES['image']);
	return render(request, "analyse.html", {'labels' : labels})
