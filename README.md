
<div align="center">
  <img src="./images/logo.png" width="300" height="150"/>
</div>

# Android Vehicle Connect App

[![Build](https://github.com/eclipse-ecsp/androidVehicleConnectApp/actions/workflows/android.yml/badge.svg)](https://github.com/eclipse-ecsp/androidVehicleConnectApp/actions/workflows/android.yml)
[![License Compliance](https://github.com/eclipse-ecsp/androidVehicleConnectApp/actions/workflows/license-compliance.yml/badge.svg)](https://github.com/eclipse-ecsp/androidVehicleConnectApp/actions/workflows/license-compliance.yml)

Vehicle Connect Application is a sample app built using the Android Vehicle Connect SDK. 
It demonstrates the API interfaces for performing remote operations on vehicles and related components.
Developers can use this sample as a reference to build their own remote operations applications.

# Table of Contents
* [Getting Started](#getting-started)
* [How to contribute](#how-to-contribute)
* [Built with Dependencies](#built-with-dependencies)
* [Code of Conduct](#code-of-conduct)
* [Authors](#authors)
* [Security Contact Information](#security-contact-information)
* [Support](#support)
* [Troubleshooting](#troubleshooting)
* [Note](#note)
* [License](#license)
* [Announcements](#announcements)


## Getting Started

Take clone of the project by using the command (git clone https://github.com/eclipse-ecsp/androidVehicleConnectApp.git) and setup the Android studio latest version.
This app demonstrate the use of vehicleConnectSDK functions.
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

The Vehicle Connect App is developed in Kotlin and is compatible with Android devices running version 7.0 (API level 24) or higher.
  
### Installation

1. Download the app source code and open the application using Android studio
2. You can find the app’s source code, including how it interacts with the SDK APIs. This can be reused as a reference for developing your own remote operations features.
3. Set up your own server and deploy the required APIs using appropriate server-side components. Once deployed, update the server details in the environment configuration file located at: app/src/main/assets/environment_file.json to run the application properly.
4. The Vehicle Connect SDK uses the OAuth library for its login flow. It is important to ensure that the URL scheme defined in the SDK's manifest file matches the Redirect URL specified in the application's environment_file.json file. Consistency between these values is essential for the authentication flow to function correctly.

### Coding style check configuration

Check the Coding Guideline document
[Android Coding Guidelines.pdf](https://github.com/user-attachments/files/16709676/Android.Coding.Guidelines.pdf)
Use android lint for code warnings

### Deployment

You can create the APK or AAB file using android studio, and install the apk file on Android device which support Android 7.0 (API level 24) or above.

## Built With Dependencies

* [VehicleConnectSDK](https://github.com/eclipse-ecsp/androidVehicleConnectSDK) - Vehicle Connect SDK library
* [Retrofit](https://github.com/square/retrofit)- For network operation Management
* [Android Lint tool] - Coding convention and style guide


## How to contribute

Please read [CONTRIBUTING.md](https://github.com/eclipse-ecsp/androidVehicleConnectApp/blob/main/CONTRIBUTING.md) for details on our contribution guidelines, and the process for submitting pull requests to us.

## Code of Conduct

Please read [CODE_OF_CONDUCT.md](https://github.com/eclipse-ecsp/androidVehicleConnectApp/blob/main/CODE_OF_CONDUCT.md) for details on our code of conduct, and the process for submitting pull requests to us.


## Authors

Check here the list of [contributors](https://github.com/eclipse-ecsp/androidVehicleConnectApp/graphs/contributors) who participated in this project.

## Security Contact Information

Please read [SECURITY.md](https://github.com/eclipse-ecsp/androidVehicleConnectApp/blob/main/SECURITY.md) to raise any security related issues.

## Support

Contact the project developers via the project's "dev" list - [ecsp-dev](https://accounts.eclipse.org/mailing-list/)

## Troubleshooting

Please read [CONTRIBUTING.md](https://github.com/eclipse-ecsp/androidVehicleConnectApp/blob/main/CONTRIBUTING.md) for details on how to raise an issue and submit a pull request to us.

## Note

License scanning for third-party libraries in the application excludes Firebase library dependencies.
As Firebase libraries are not open source, they are rejected by the scanning tool. Consequently, these dependencies were removed from the dependency collection list prior to initiating the license scan.

Firebase libraries are utilized to implement the notification flow within the application.

## License

This project is licensed under the Apache-2.0 License - see the [LICENSE](https://github.com/eclipse-ecsp/androidVehicleConnectApp/blob/main/LICENSE) file for details.

## Announcements

All updates to this library are present in our [releases page](https://github.com/eclipse-ecsp/androidVehicleConnectApp/releases). 
For the versions available, see the [tags on this repository](https://github.com/eclipse-ecsp/androidVehicleConnectApp/tags).

