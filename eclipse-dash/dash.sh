#!/bin/bash

echo 'Checking All Dependencies'

chmod +x 'gradlew'

echo 'Checking dependencies for SDK implemented sample app Module'

'./gradlew' 'app:dependencies' | 'grep' '-Poh' "(?<=\-\-\- ).*" | 'grep' '-Pv' "\([c\*]\)" | 'grep' '-Pv' "\([n\*]\)" | 'perl' '-pe' 's/([\w\.\-]+):([\w\.\-]+):(?:[\w\.\-]+ -> )?([\w\.\-]+).*$/$1:$2:$3/gmi;t' | 'sort' -u | 'grep' '-v' 'project :androidVehicleConnectSDK' | 'grep' '-v' 'com.google.android.gms:play-services-basement:16.0.1' | 'grep' '-v' 'com.google.android.gms:play-services-basement:18.3.0' | 'grep' '-v' 'com.google.android.gms:play-services-tasks:16.0.1' | 'grep' '-v' 'com.google.android.gms:play-services-tasks:18.1.0' | 'grep' '-v' 'com.google.android.datatransport:transport-api:3.0.0' | 'grep' '-v' 'com.google.android.gms:play-services-base:18.0.1' | 'grep' '-v' 'com.google.android.gms:play-services-basement:18.0.0' | 'grep' '-v' 'com.google.android.gms:play-services-cloud-messaging:17.2.0' | 'grep' '-v' 'com.google.android.gms:play-services-stats:17.0.2' | 'grep' '-v' 'com.google.android.gms:play-services-tasks:18.0.1' | 'grep' '-v' 'com.google.firebase:firebase-annotations:16.2.0' | 'grep' '-v' 'com.google.firebase:firebase-iid-interop:17.1.0' | 'grep' '-v' 'com.google.firebase:firebase-measurement-connector:19.0.0' > 'AppDependencies'

echo 'Tool checking for SDK dependencies'

chmod +x $PWD/eclipse-dash/dash.jar $PWD/AppDependencies
java -jar $PWD/eclipse-dash/dash.jar $PWD/AppDependencies -summary $PWD/APP_NOTICE.md
#