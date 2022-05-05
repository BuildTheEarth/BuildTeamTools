# Generate Houses with Commands

To generate a simple random house, enter:

```
/gen house
```

## Block Parameters

The Block Parameters define the blocks of different parts of the building. If you don't enter them it will choose a random block from a custom block palette.

### Wall Block [-w]

The Wall Block defines the main color of the facade of the building.
To change the wall color of the building, add the `-w` flag:
```
/gen house -w <id>
```
**Default:** A random wall block <br>
To find the correct ID of the block you want to use, click [here](https://www.digminecraft.com/lists/item_id_list_pc_1_12.php)


### Roof Block [-r]

The Roof Block of the building. Please note that some blocks will only make sense for some roof types. <br>
For the `FLAT` roof type use full blocks.<br>
For the `SLABS` roof type use slabs.<br>
For the `STAIRS` roof type use stairs.<br>
To change the wall color of the building, add the `-r` flag:
```
/gen house -r <id>
```
**Default:** A random roof block depending on the selected roof type<br>
To find the correct ID of the block you want to use, click [here](https://www.digminecraft.com/lists/item_id_list_pc_1_12.php)

### Base Block [-b]

The Base Block defines the base at the bottom of the building.
To change the base color of the building, add the `-b` flag:
```
/gen house -b <id>
```
**Default:** A random base block <br>
To find the correct ID of the block you want to use, click [here](https://www.digminecraft.com/lists/item_id_list_pc_1_12.php)

### Window Block [-wd]

The Window Block defines the block that is used for the windows of the building.
To change the Window color of the building, add the `-wd` flag:
```
/gen house -wd <id>
```
**Default:** 95:15 (*minecraft:black_stained_glass*) <br>
To find the correct ID of the block you want to use, click [here](https://www.digminecraft.com/lists/item_id_list_pc_1_12.php)
<br>
<br>


## Size Parameters

### Floor Count [-fc]

The Floor Count Parameter defines the numbers of floors of the building.
To change the floor count, add the `-fc` flag:
```
/gen house -fc <amount>
```

### Floor Height [-fh]

The Floor Height Parameter defines the height of each floor of the building.
To change the floor height of the building, add the `-fh` flag:
```
/gen house -fh <height>
```
**Default:** 3

### Base Height [-bh]

The Base Height Parameter defines the height of the base at the bottom of the building.
To change the height of the base of the building, add the `-bh` flag:
```
/gen house -bh <height>
```
**Default:** 1

### Window Height [-wdh]

The Window Height Parameter defines the height of the windows of the building.
To change the window height of the building, add the `-wdh` flag:
```
/gen house -wdh <height>
```
**Default:** 2

### Window Width [-wdw]

The Window Width Parameter defines the width of the windows of the building.
To change the window width of the building, add the `-wdw` flag:
```
/gen house -wdw <width>
```
**Default:** 2

### Window Distance [-wdd]

The Window Distance Parameter defines the spacing between the windows of the building.
To change the window distance of the building, add the `-wdd` flag:
```
/gen house -wdd <distance>
```
**Default:** 2

### Max Roof Height [-mrh]

The Max Roof Height Parameter defines the maximum height of the roof of the building.
This can be useful for large buildings with a gable roof that should not extend a certain height.
To change the maximum roof height of the building, add the `-mrh` flag:
```
/gen house -mrh <distance>
```
**Default:** 10


## Other Parameters

### Roof Type [-rt]

To change the roof type of the building, add the -rt flag:
```
/gen house -rt <type>
```
**Default:** A random roof type <br>

**Roof Types**:
- FLAT - A flat roof surrounded with carpet 
- SLABS - A flat gable roof made out of slabs
- STAIRS - A gable roof made out of stairs
