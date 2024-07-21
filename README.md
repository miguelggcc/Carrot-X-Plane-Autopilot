# Carrot-X-Plane-Autopilot: Autopilot in Java with GUI

## Overview
This project consists of an autopilot for X-Plane to follow a flight plan defined by waypoints, including both lateral and vertical guidance. A graphical interface displays the aircraft's position and route.

<p align="center">
  <img src="https://github.com/user-attachments/assets/89841e74-f797-4f1d-9754-2c81df20921d" width="750">
</p>

## Features

* **Lateral Guidance:** Implements the ‘Carrot Algorithm’, where the aircraft follows a moving reference point (the ‘carrot’) along the desired path by providing the necessary lateral acceleration and roll angle to minimize the cross-track error.

* **Vertical Guidance:** Uses a double PID control loop for elevator actions, maintaining a constant climb speed and a cruise altitude of 2000 ft.

* **Graphical Interface:** Features a GUI to display the aircraft’s position and route, with options to save and visualize the route in MATLAB and Google Earth.


## References
- Ducard, G. J. (2009). *Fault-tolerant flight control and guidance systems*.
- Kothari, M. (2011). *Algorithms for Motion Planning and Target Capturing*.
- Spitzer, C., Ferrell, U., & Ferrell, T. (2017). *Digital avionics handbook*.
- L. Cantoni, "X-Pi: A Java-based autopilot for X-Plane," GitHub repository, 2021. Available: https://github.com/luizcantoni/x-pi
