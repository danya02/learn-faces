#!/usr/bin/python3
import os
import pygame
import pygame.image
import json
import warnings

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
        if i[0] =< input_width:
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


if __name__ == '__main__':
    print("To be implemented.")
