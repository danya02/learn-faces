#!/usr/bin/python3
import os
import sys
import pygame
import pygame.image
import json
import warnings
import random
import time
import socket
import uuid

pygame.init()
warnings.simplefilter('always')
warnings.simplefilter('ignore', ImportWarning)

DATAFILE = './database.json'
ASSETDIR = '.'
LOGFILE = './backlog.tmp'
LOGSERVER = 'danya02.ru'
LOGPORT = 1337
UUIDFILE = './thishost.uuid'
MY_UUID = ''

def register(uuid):
    s = socket.create_connection((LOGSERVER, LOGPORT))
    f = s.makefile("r+")
    f.write("REG\n")
    f.flush()
    if f.read(1) != "0":
        return 1
    f.write(MY_UUID + "::" + str(time.time()) + "::" + "register\n")
    f.flush()
    f.close()
    s.shutdown()

def user_init():
    try:
        with open(UUIDFILE) as o:
            MY_UUID = o.read()
    except:
        MY_UUID = str(uuid.uuid4())
        with open(UUIDFILE, 'w') as o:
            o.write(MY_UUID)
            register(MY_UUID)

def submit_data(data):
    try:
        s = socket.create_connection((LOGSERVER, LOGPORT))
        f = s.makefile("r+")
        f.write(MY_UUID+"\n")
        f.flush()
        if f.read(1) != "0":
            raise ConnectionAbortedError
        with open(LOGFILE) as o:
            f.write(o.read())
        f.write(MY_UUID + "::" + str(time.time()) + "::" + str(data) + "\n")
        f.flush()
        f.close()
        s.shutdown()
        return 0
    except ConnectionRefusedError:
        with open(LOGFILE, "a") as o:
            o.write(MY_UUID + "::" + str(time.time()) + "::" + str(data) + "\n")
        return 1

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
        if int(i[0]) <= input_width:
            if int(i[1]) <= input_height:
                new_delta =  (input_width * input_height) - (int(i[0]) * int(i[1]))
                if new_delta < delta:
                    delta = new_delta
                    used_table = i
    if used_table is None:
        raise OSError(os.errno.ENOENT, 'Unable to find any matching table.')
    if not delta == 0:
        warnings.warn('No exact table found; using one with delta of ' + str(delta), RuntimeWarning)
    base_surface = pygame.image.load(os.path.join(ASSETDIR, 'table_'+str(used_table[0])+'x'+str(used_table[1])+'.png'))
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

def draw_text(surface, text, color, rect, font, aa=False, bkg=None):
    rect = pygame.Rect(rect)
    y = rect.top
    lineSpacing = -2
    fontHeight = font.size("Tg")[1]
    while text:
        i = 1
        if y + fontHeight > rect.bottom:
            break
        while font.size(text[:i])[0] < rect.width and i < len(text):
            i += 1
        if i < len(text):
            i = text.rfind(" ", 0, i) + 1
        if bkg:
            image = font.render(text[:i], 1, color, bkg)
            image.set_colorkey(bkg)
        else:
            image = font.render(text[:i], aa, color)
        surface.blit(image, (rect.left, y))
        y += fontHeight + lineSpacing
        text = text[i:]
    return text

def generate_question(lv, users):
    userlist = users.copy()
    random.shuffle(userlist)
    if lv == 1:
        answer = userlist.pop()
        image_prequestion = pygame.image.load(answer["main_pic"])
        str_prequestion = "Memorise this face."
        str_question = "Which face was displayed before?"
        wrong_ans1 = pygame.image.load(userlist.pop()['main_pic'])
        wrong_ans2 = pygame.image.load(userlist.pop()['main_pic'])
        wrong_ans3 = pygame.image.load(userlist.pop()['main_pic'])
        correct_answer = random.choice([(0,0), (1,0), (0,1), (1,1)])
        if correct_answer == (0,0):
            image_question = embed_into_table([[image_prequestion, wrong_ans1], [wrong_ans2, wrong_ans3]])
        elif correct_answer == (1,0):
            image_question = embed_into_table([[wrong_ans1, image_prequestion], [wrong_ans2, wrong_ans3]])
        elif correct_answer == (0,1):
            image_question = embed_into_table([[wrong_ans1, wrong_ans2], [image_prequestion, wrong_ans3]])
        elif correct_answer == (1,1):
            image_question = embed_into_table([[wrong_ans1, wrong_ans2], [wrong_ans3, image_prequestion]])
        return {'str_prequestion': str_prequestion, 'image_prequestion': image_prequestion, 'str_question': str_question, 'image_question': image_question, 'correct_answer': correct_answer, 'width': 2, 'height': 2}
    elif lv == 2:
            answer = userlist.pop()
            image_prequestion = pygame.image.load(answer["main_pic"])
            str_prequestion = "Memorise this face."
            str_question = "Which face was displayed before?"
            right_ans = pygame.image.load(random.choice(answer["alt_pics"]))
            wrong_ans1 = pygame.image.load(random.choice(userlist.pop()['alt_pics']))
            wrong_ans2 = pygame.image.load(random.choice(userlist.pop()['alt_pics']))
            wrong_ans3 = pygame.image.load(random.choice(userlist.pop()['alt_pics']))
            correct_answer = random.choice([(0,0), (1,0), (0,1), (1,1)])
            if correct_answer == (0,0):
                image_question = embed_into_table([[right_ans, wrong_ans1], [wrong_ans2, wrong_ans3]])
            elif correct_answer == (1,0):
                image_question = embed_into_table([[wrong_ans1, right_ans], [wrong_ans2, wrong_ans3]])
            elif correct_answer == (0,1):
                image_question = embed_into_table([[wrong_ans1, wrong_ans2], [right_ans, wrong_ans3]])
            elif correct_answer == (1,1):
                image_question = embed_into_table([[wrong_ans1, wrong_ans2], [wrong_ans3, right_ans]])
            return {'str_prequestion': str_prequestion, 'image_prequestion': image_prequestion, 'str_question': str_question, 'image_question': image_question, 'correct_answer': correct_answer, 'width': 2, 'height': 2}
    elif lv == 3:
            str_prequestion = "Memorise these people."
            right_person = userlist.pop()
            str_question = "Which of these is " + right_person["name"] + "?"
            wrong_person1 = userlist.pop()
            wrong_person2 = userlist.pop()
            pic1 = pygame.image.load(right_person["main_pic"])
            pic2 = pygame.image.load(wrong_person1["main_pic"])
            pic3 = pygame.image.load(wrong_person2["main_pic"])
            rect = pygame.Rect(0, 300, 350, 160)
            pic1.fill(pygame.Color(0, 0, 0, 127), rect)
            pic2.fill(pygame.Color(0, 0, 0, 127), rect)
            pic3.fill(pygame.Color(0, 0, 0, 127), rect)
            draw_text(pic1, right_person["name"], pygame.Color(255, 255, 255), rect, pygame.font.SysFont("arial", 30))
            draw_text(pic2, wrong_person1["name"], pygame.Color(255, 255, 255), rect, pygame.font.SysFont("arial", 30))
            draw_text(pic3, wrong_person2["name"], pygame.Color(255, 255, 255), rect, pygame.font.SysFont("arial", 30))
            k = [pic1, pic2, pic3]
            random.shuffle(k)
            image_prequestion = embed_into_table([k])
            right_ans = pygame.image.load(random.choice(right_person["alt_pics"]))
            wrong_ans1 = pygame.image.load(random.choice(wrong_person1["alt_pics"]))
            wrong_ans2 = pygame.image.load(random.choice(wrong_person2["alt_pics"]))
            wrong_ans3 = pygame.image.load(random.choice(userlist.pop()["alt_pics"]))
            correct_answer = random.choice([(0,0), (1,0), (0,1), (1,1)])
            if correct_answer == (0,0):
                k = [wrong_ans2, wrong_ans3]
                random.shuffle(k)
                image_question = embed_into_table([[right_ans, wrong_ans1], k])
            elif correct_answer == (1,0):
                k = [wrong_ans2, wrong_ans3]
                random.shuffle(k)
                image_question = embed_into_table([[wrong_ans1, right_ans], k])
            elif correct_answer == (0,1):
                k = [wrong_ans1, wrong_ans2]
                random.shuffle(k)
                image_question = embed_into_table([k, [right_ans, wrong_ans3]])
            elif correct_answer == (1,1):
                k = [wrong_ans1, wrong_ans2]
                random.shuffle(k)
                image_question = embed_into_table([k, [wrong_ans3, right_ans]])
            return {'str_prequestion': str_prequestion, 'image_prequestion': image_prequestion, 'str_question': str_question, 'image_question': image_question, 'correct_answer': correct_answer, 'width': 2, 'height': 2}
    else:
        raise NotImplementedError("Difficulty level " + str(lv) + "not implemented")

def display_question(lv, database):
    question = generate_question(lv, database["userlist"])
    frames = {'yellow': pygame.image.load(os.path.join(ASSETDIR, 'yellow_frame.png')), 'red': pygame.image.load(os.path.join(ASSETDIR, 'red_frame.png')), 'green': pygame.image.load(os.path.join(ASSETDIR, 'green_frame.png'))}
    display = pygame.display.set_mode(question["image_prequestion"].get_size())
    pygame.display.set_caption(question['str_prequestion'], "Quiz")
    display.blit(question["image_prequestion"], (0, 0))
    pygame.display.flip()
    for i in range(lv*5*100):
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                pygame.quit()
                sys,exit(0)
            elif event.type == pygame.KEYDOWN:
                if event.key == pygame.K_ESCAPE:
                    pygame.quit()
                    sys,exit(0)
        time.sleep(0.01)
    display = pygame.display.set_mode(question["image_question"].get_size())
    pygame.display.set_caption(question['str_question'], "Quiz")
    item_selected = False
    hor_min = 39
    ver_min = 39
    hor_now = hor_min
    ver_now = ver_min
    hor_delta = 351
    ver_delta = 461
    hor_max = hor_min + (hor_delta * (question['width']-1))
    ver_max = ver_min + (ver_delta * (question['height']-1))
    while not item_selected:
        time.sleep(0.01)
        display.blit(question["image_question"], (0, 0))
        display.blit(frames['yellow'], (hor_now, ver_now))
        pygame.display.flip()
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                pygame.quit()
                sys,exit(0)
            elif event.type == pygame.KEYDOWN:
                if event.key == pygame.K_ESCAPE:
                    pygame.quit()
                    sys,exit(0)
                elif event.key == pygame.K_w or event.key == pygame.K_k or event.key == pygame.K_UP:
                    ver_now -= ver_delta
                    ver_now = max(ver_min, ver_now)
                elif event.key == pygame.K_s or event.key == pygame.K_j or event.key == pygame.K_DOWN:
                    ver_now += ver_delta
                    ver_now = min(ver_max, ver_now)
                elif event.key == pygame.K_a or event.key == pygame.K_h or event.key == pygame.K_LEFT:
                    hor_now -= hor_delta
                    hor_now = max(hor_min, hor_now)
                elif event.key == pygame.K_d or event.key == pygame.K_l or event.key == pygame.K_RIGHT:
                    hor_now += hor_delta
                    hor_now = min(hor_max, hor_now)
                elif event.key == pygame.K_RETURN or event.key == pygame.K_SPACE:
                    item_selected = True
    selected_answer = (int((hor_now - hor_min) / hor_delta), int((ver_now - ver_min) / ver_delta))
    if selected_answer == question['correct_answer']:
        display.blit(frames['green'], (hor_now, ver_now))
        pygame.display.flip()
        time.sleep(5)
        return True
    else:
        display.blit(frames['red'], (hor_now, ver_now))
        display.blit(frames['green'], (hor_min+(hor_delta * question['correct_answer'][0]), ver_min+(ver_delta * question['correct_answer'][1])))
        pygame.display.flip()
        time.sleep(5)
        return False

if __name__ == '__main__':
    user_init()
    submit_data("app_start")
    try:
        with open(DATAFILE) as i:
            data = json.load(i)
        i=1
        while 1:
            i += (1 if display_question(i, data) else 0)
    except NotImplementedError:
        print("There are no more questions.")
    submit_data("app_start")
