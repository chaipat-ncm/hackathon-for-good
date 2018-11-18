from django import forms
from django.forms import ModelForm, ClearableFileInput

from ncia.models import ImageUploader


class ImageUploaderForm(ModelForm):

    class Meta:
        model = ImageUploader
        fields = ('image',)
        widgets={'image': ClearableFileInput(attrs={'multiple': True})}

