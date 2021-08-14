# SLAC-2020
This contains the project collaboration for SLAC 2020's ***Try Code;Catch Bugs*** team

### Theme: Healthcare
#### Problem Statement
To bridge the communication gap between patients in need of immediate care (an emergency situation) and the hospital.

#### Description
Our project aims at solving the crucial problems of emergencies in health care such as Triage, Registration and Treatment. 
We provide a solution for hospitals to prepare before-hand for any incoming patient, in dire need of care.
Our project prototype acts as a widget that can be integrated into the existing management system for hospitals
An additional feature of this widget allows the hospitals to send alerts to the general outpatients informing them of their turn

#### Code Component

##### 1. Medify
Contains the Android APK which holds the form whose details will be filled by the paramedic. The entered details will then be sent to the SQL server.
##### 2. slac-Backend
Consists of Java APIs which is used to alterantively fetch and send data provided by the Angular JS.
##### 3. slac-Frontend
Consists of Four main .html files which take care of the Emergency patients information, Bed allocation for the patients, OPD patiends information and a page for alerting blood donors in the time of need.
