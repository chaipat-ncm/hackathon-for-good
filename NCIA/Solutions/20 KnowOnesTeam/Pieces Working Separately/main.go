package main

import (
	"context"
	"fmt"
	"io"
	"log"
	"os"
	"path/filepath"
	"sync"

	"cloud.google.com/go/storage"
	vision "cloud.google.com/go/vision/apiv1"
	"google.golang.org/api/iterator"
	"google.golang.org/api/option"
)

// An ObjectIterator is an iterator over ObjectAttrs.
type ObjectIterator struct {
	ctx      context.Context
	bucket   *BucketHandle
	query    storage.Query
	pageInfo *iterator.PageInfo
	nextFunc func() error
	items    []*storage.ObjectAttrs
}

// BucketHandle provides operations on a Google Cloud Storage bucket.
// Use Client.Bucket to get a handle.
type BucketHandle struct {
	c                *storage.Client
	name             string
	acl              storage.ACLHandle
	defaultObjectACL storage.ACLHandle
	conds            *storage.BucketConditions
	userProject      string // project for Requester Pays buckets
}

func listIterator(w io.Writer, client *storage.Client, bucket string) *storage.ObjectIterator {
	ctx := context.Background()
	// [START storage_list_files]
	it := client.Bucket(bucket).Objects(ctx, nil)
	return it
}

func findLabels(file string) ([]string, error) {
	// [START init]
	ctx := context.Background()

	// Create the client.
	client, err := vision.NewImageAnnotatorClient(ctx, option.WithCredentialsFile("hack-peace-justice-security-45870a3cf3d5.json"))
	if err != nil {
		return nil, err
	}
	// [END init]

	// [START request]
	gsFilePath := fmt.Sprintf(`gs://peace-photos/%s`, file)
	image := vision.NewImageFromURI(gsFilePath)

	// Perform the request.

	annotations, err := client.DetectLabels(ctx, image, nil, 20)
	if err != nil {
		return nil, err
	}
	// [END request]
	// [START transform]
	var labels []string
	for _, annotation := range annotations {
		labels = append(labels, annotation.Description)
	}
	return labels, nil
	// [END transform]
}

func getLabels() {

	ctx := context.Background()
	client, err := storage.NewClient(ctx, option.WithCredentialsFile("hack-peace-justice-security-45870a3cf3d5.json"))
	if err != nil {
		log.Println(err)
	}
	defer client.Close()

	it := listIterator(os.Stdout, client, "peace-photos")
	//	var class string
	var wg sync.WaitGroup
	for i := 0; ; i++ {
		attrs, err := it.Next()
		if err == iterator.Done {
			break
		}
		if err != nil {
			log.Println(err)
		}
		wg.Add(1)
		go func(i int, attrs *storage.ObjectAttrs) {
			fmt.Println(attrs.Name)
			class := filepath.Dir(attrs.Name)
			filename := filepath.Base(attrs.Name)
			f, err := os.Create(fmt.Sprintf("labels/%03d-%s.txt", i, filename))
			if err != nil {
				log.Println(err)
			}
			defer f.Close()

			labels, err := findLabels(attrs.Name)
			if err != nil {
				log.Println(err)
			}
			for n := range labels {
				// if i < 234 {
				// 	class = "propaganda"
				// }
				// class = "notpropaganda"
				fmt.Fprintf(f, "%s,%s,%d,%s\n", fmt.Sprintf("%03d,%s.txt", i, attrs.Name), class, n, labels[n])
			}
			wg.Done()
		}(i, attrs)
	}
	wg.Wait()
	// [END storage_list_files]
}

func main() {
	//	getLabels()

}
