import random

image_percentage_chance = 0.85


def decide(img_assesment, txt_assesment):
    print("img " + str(img_assesment))
    print("itxt " + str(txt_assesment))

    if img_assesment and txt_assesment:
        return True
    if not img_assesment and not txt_assesment:
        return False
    if random.random() < image_percentage_chance:
        return img_assesment
    else:
        return txt_assesment
