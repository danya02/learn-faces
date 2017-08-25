#!/usr/bin/python3
import os
import pygame
import pygame.image
import json
import warnings
import random
import time

pygame.init()
warnings.simplefilter('always')

DATAFILE = './database.json'
ASSETDIR = '.'

def embed_into_table(images):
    tables = []
    for i in os.listdir(ASSETDIR):
        if 'table_' in i:
            tables += [i]
    tables = [tuple(i.split('_')[1].split('.')[0].split('x')) for i in tables]
    try:
        if not isinstance(images[0][0], pygame.Surface):
            raise TypeError
    except:
        raise TypeError("images must be a list of lists of Surfaces")
    input_width = set()
    for i in images:
        input_width.add(len(i))
    if len(input_width)>1:
        raise ValueError("widths unequal")
    input_width = input_width.pop()
    input_height = len(images)
    used_table = None
    delta = 65535
    new_delta = None
    for i in tables:
        if i[0] <= input_width:
            if i[1] <= input_height:
                new_delta = (i[0] * i[1]) - (input_width * input_height)
                if new_delta < delta:
                    delta = new_delta
                    used_table = i
    if used_table is None:
        raise OSError(os.errno.ENOENT, 'Unable to find any matching table.')
    if not delta == 0:
        warnings.warn('No exact table found; using one with delta of ' + str(delta), RuntimeWarning)
    base_surface = pygame.image.load(os.path.join(ASSETDIR, 'table_'+str(used_table[0])+'x'+str(used_table[0])+'.png'))
    start_width = 39
    start_height = 39
    delta_width = 351
    delta_height = 461
    current_width = start_width
    current_height = start_height
    for i in images:
        for j in i:
            base_surface.blit(j, (current_width, current_height))
            current_width += delta_width
        current_width = start_width
        current_height += delta_height
    return base_surface

def generate_question(lv, userlist):
    if lv == 1:
        answer = random.choice(userlist)
        image_prequestion = surface.image.load(answer["main_pic"])
        str_prequestion = "Memorise this face."
        str_question = "Which face was displayed before?"
        wrong_ans1 = pygame.image.load(random.choice(userlist)['main_pic'])
        wrong_ans2 = pygame.image.load(random.choice(userlist)['main_pic'])
        wrong_ans3 = pygame.image.load(random.choice(userlist)['main_pic'])
        correct_answers = random.choice([('A1', '1A'), ('A2', '2A'), ('B1', '1B'), ('B2', '2B')])
        if correct_answers == ('A1', '1A'):
            image_question = embed_into_table([[image_prequestion, wrong_ans1], [wrong_ans2, wrong_ans3]])
        elif correct_answers == ('A2', '2A'):
            image_question = embed_into_table([[wrong_ans1, image_prequestion], [wrong_ans2, wrong_ans3]])
        elif correct_answers == ('B1', '1B'):
            image_question = embed_into_table([[wrong_ans1, wrong_ans2], [image_prequestion, wrong_ans3]])
        elif correct_answers == ('B2', '2B'):
            image_question = embed_into_table([[wrong_ans1, wrong_ans2], [wrong_ans3, image_prequestion]])
        return {'str_prequestion': str_prequestion, 'image_prequestion': image_prequestion, 'str_question': str_question, 'image_question': image_question, 'correct_answers': correct_answers}
    elif lv == 2:
            answer = random.choice(userlist)
            image_prequestion = surface.image.load(answer["main_pic"])
            str_prequestion = "Memorise this face."
            str_question = "Which face was displayed before?"
            wrong_ans1 = pygame.image.load(random.choice(random.choice(userlist)['alt_pic']))
            wrong_ans2 = pygame.image.load(random.choice(random.choice(userlist)['alt_pic']))
            wrong_ans3 = pygame.image.load(random.choice(random.choice(userlist)['alt_pic']))
            correct_answers = random.choice([('A1', '1A'), ('A2', '2A'), ('B1', '1B'), ('B2', '2B')])
            if correct_answers == ('A1', '1A'):
                image_question = embed_into_table([[image_prequestion, wrong_ans1], [wrong_ans2, wrong_ans3]])
            elif correct_answers == ('A2', '2A'):
                image_question = embed_into_table([[wrong_ans1, image_prequestion], [wrong_ans2, wrong_ans3]])
            elif correct_answers == ('B1', '1B'):
                image_question = embed_into_table([[wrong_ans1, wrong_ans2], [image_prequestion, wrong_ans3]])
            elif correct_answers == ('B2', '2B'):
                image_question = embed_into_table([[wrong_ans1, wrong_ans2], [wrong_ans3, image_prequestion]])
            return {'str_prequestion': str_prequestion, 'image_prequestion': image_prequestion, 'str_question': str_question, 'image_question': image_question, 'correct_answers': correct_answers}
    else:
        raise NotImplementedError("Difficulty level " + str(lv) + "not implemented")

def display_question(lv, userlist):
    question = generate_question(lv, userlist)
    display = pygame.display.set_mode(resolution=question["image_prequestion"])
    print(question['str_prequestion'])
    display.blit(question["image_prequestion"], (0, 0))
    time.sleep(10 * lv)
    for i in range(80):
        print()
    display = pygame.display.set_mode(resolution=question["image_prequestion"])
    print(question["str_question"])
    display.blit(question["image_prequestion"], (0, 0))
    answer = input("answer> ")
    display.fill(pygame.Color(255,255,255,255))
    if answer.lower() in question['correct_answers']:
        print("Answer correct.")
        return True
    else:
        print("Answer incorrect.")

if __name__ == '__main__':
    try:
        data = json.load(open(DATAFILE))["userlist"]
        i=0
        while 1:
            i += 1
            display_question(i, data)
    except NotImplementedError:
        print("There are no more questions.")
