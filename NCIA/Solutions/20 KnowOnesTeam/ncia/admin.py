from django.contrib import admin

from django.contrib import admin
from .models import ImageUploader

# ROBOCOP: fixit
#@admin.register(ImageUploader)
#class ImageFileInline(admin.TabularInline):
#    model = ImageUploader

# ROBOCOP: fixit
#class FeedAdmin(admin.ModelAdmin):
#    inlines = [
#        ImageFileInline,
#    ]

