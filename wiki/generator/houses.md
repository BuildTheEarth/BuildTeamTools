# Generate Houses with Commands

To generate a simple random house, enter:

```
/gen house
```

## Block Parameters

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

The Base Block defines the Base at the bottom of the Building.
To change the base color of the building, add the `-b` flag:
```
/gen house -b <id>
```
**Default:** A random base block <br>
To find the correct ID of the block you want to use, click [here](https://www.digminecraft.com/lists/item_id_list_pc_1_12.php)
<br>
<br>


## Size Parameters

### Floor Count [-fc]

To change the numbers of floors of the building, add the `-fc` flag:
```
/gen house -fc <amount>
```

### Floor Height [-fh]

To change the height of each floor of the building, add the `-fh` flag:
```
/gen house -fh <height>
```

### Base Height [-bh]

To change the height of the base of the building, add the `-bh` flag:
```
/gen house -bh <height>
```

### Window Height [-wdh]

To change the height of the windows of the building, add the `-wdh` flag:
```
/gen house -wdh <height>
```

### Window Height [-wdw]

To change the width of the windows of the building, add the `-wdw` flag:
```
/gen house -wdw <width>
```

### Window Distance [-wdd]

To change the spacing between the windows of the building, add the `-wdd` flag:
```
/gen house -wdd <distance>
```

### Max Roof Height [-mrh]

To change the spacing between the windows of the building, add the `-mrh` flag:
```
/gen house -mrh <distance>
```


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
