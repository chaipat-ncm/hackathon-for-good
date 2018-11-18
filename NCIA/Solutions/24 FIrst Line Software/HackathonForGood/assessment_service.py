import process_image_cnn
import extract_text
import analyze_text
import binary_decider
import os


def assess(directory, image_name):
    img_assessment = process_image_cnn.is_negative(os.path.join(directory, image_name))

    image_content = extract_text.to_file_contents(directory, image_name)
    txt_assessment = analyze_text.predict(image_content.content)

    return binary_decider.decide(img_assessment, txt_assessment)
