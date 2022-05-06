# BTE Generator

This module lets you generate complex structures using server side scripts.
If used correctly it allows you to speed up your building process up to 70% and skip boring and repetetive parts of the construction.
Especially in villages where all buildings are similar rectangle boxes and the roads are very basic it allows you to create entire disticts in no time.
For example the House Generator creates the shell, windows and roof of a given Building outline with a few clicks.
Just by entering a few parameters you can quickly generate a buildings, roads, railways and more.

### Components
- [House Generator](houses/README.md)
- Road Generator (TODO)
- Railway Generator (TODO)
- Tree Generator (TODO)

### How to use

For each generator you can either use it with an interactive **UI** or via **Command**.

The **UI** lets you to select one of the generators and set the necessary parameters by clicking through the inventories.<br>
Especially for new builders this is an easy way to click together the building you need without much background knowledge.
To open up the UI, simply enter:
```
/gen
```

With **Commands** you can further customize the parameters. This makes it possible to create more precise buildings and a much quicker workflow.
The general structure of the command looks like this:
```
/gen <component> -parameters
```

Further instructions for the UI and the Command Usage can be found in the description for each specific Components.