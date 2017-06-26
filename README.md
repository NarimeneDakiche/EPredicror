![Logo](./logo.jpg)

# EPredictor

EPredictor is a tool that does the prediction of community evolution in social networks using classification algorithms. 

EPredictor grants the exection of prediction steps while dealing with the link between them, by starting the processus from any step, by importing and exporting all kinds of data and by visualizing results of different methods.


## Prediction Process
The process of predicting the evolution of communities consists of four steps:

1. **Segmentation of data**: where the data is split into snapshots covering periods of time.
2. **Detection of communities**: by extracting communities from each Snapshot. 
3. **Identification of community changes**: over time between each two consecutive snapshots and creation of evolution chains of communities.
4. **The prediction of future community evolution**: by creating a prediction model created to predict the next event a community will manifest.

## Motivation
In their researchs, researchers perform each phase of the prediction process separately and they link manually between the inputs and outputs of the methods. The objective of EPredictor is to predict the evolution of communities within social networks. 

## Contributing

Fork, implement, add tests, pull request, get our everlasting thanks and a respectable place here :).

## Documentation

View the [EPredictor User Guide](https://esi.dz). 

## License

EPredictor is released under the [MIT License](https://github.com/abdelouahab1/EPredictor/blob/master/LICENSE).
