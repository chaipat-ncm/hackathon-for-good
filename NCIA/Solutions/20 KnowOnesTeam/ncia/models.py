from django.db import models


class ImageUploader(models.Model):
    image = models.ImageField(upload_to='uploads/', null=True, blank=True)
    json_received = models.TextField()
