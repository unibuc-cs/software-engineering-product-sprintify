package com.example.runpath.ui.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.runpath.database.CircuitDAO
import com.example.runpath.database.SessionManager
import com.example.runpath.models.Circuit
import com.google.maps.model.LatLng


@Composable
fun CircuitsPage(navController: NavController,sessionManager: SessionManager) {

    val circuitDao = CircuitDAO()



    val circuitTineretului = Circuit(
        name = "Tineretului Park",
        description = "A beautiful run through the park",
        distance =1550.0,
        estimatedTime = "10 min",
        intensity = 2,
        terrain = "Flat",
        petFriendly = true,
        lightLevel = 3,
        rating = 4.5,
        difficulty = 2,
        route = listOf(
            LatLng(44.40642361849343, 26.100585489754483),
            LatLng(44.40629197849722,26.100589780896005 ),
            LatLng(44.406105152582775,26.100722818070242),
            LatLng(44.405985775399515,26.10085585524448),
            LatLng(44.40585413441797,26.101134804982028),
            LatLng(44.40573475672245,26.10134508990686),
            LatLng(44.405566324338714,26.101521042425336),
            LatLng(44.405379496106654,26.10161974784805),
            LatLng(44.404928997833984,26.101933029991958),
            LatLng(44.40463179336069,26.102160481447388),
            LatLng(44.404297796444055,26.10238793290282),
            LatLng(44.40408030446606,26.102469471794937),
            LatLng(44.403887339621164,26.10262825843765),
            LatLng(44.40369437413977,26.102847126300365),
            LatLng(44.40351367206978,26.103057411225198),
            LatLng(44.40340042151658,26.103284862680628),
            LatLng(44.403274906648754,26.103555229480296),
            LatLng(44.403186183868996,26.103877094562083),
            LatLng(44.40314651723566,26.10425904086387),
            LatLng(44.403174302911104,26.104718234916252),
            LatLng(44.403232748728165,26.105452086909825),
            LatLng(44.40333411868052,26.10603144405704),
            LatLng(44.40347228040074,26.10655930292214),
            LatLng(44.40361657392447,26.10705282938088),
            LatLng(44.40383445078085,26.10758927118386),
            LatLng(44.4041994931432,26.10813429723439),
            LatLng(44.40455840143117,26.108310249752865),
            LatLng(44.40504607619036,26.108280209142865),
            LatLng(44.405527615189115,26.10824158494015),
            LatLng(44.40598462317191,26.10816862898591),
            LatLng(44.40636191556774,26.108344581504387),
            LatLng(44.406604308767925,26.10781672263929),
            LatLng(44.40643587888793,26.107718017216573),
            LatLng(44.40624905343308,26.107172992475714),
            LatLng(44.405976383200056,26.106688048954855),
            LatLng(44.40571597504833,26.106434848030947),
            LatLng(44.405529147294224,26.106464888640946),
            LatLng(44.40539137334197,26.10694124856909),
            LatLng(44.405167753697086,26.106868292614852),
            LatLng(44.404888946428,26.106520678064587),
            LatLng(44.40473890741339,26.106018568667967),
            LatLng(44.40469310981656,26.105293298957438),
            LatLng(44.40476381790208,26.104593778715387),
            LatLng(44.40496942674557,26.103842760191217),
            LatLng(44.40529767125325,26.103143239949166),
            LatLng(44.405448091978485,26.10276987658526),
            LatLng(44.405751806650606,26.10249092684771),
            LatLng(44.40597926037779,26.102357892947655),
            LatLng(44.40635042119953,26.10247376424613),
            LatLng(44.40667252614927,26.102383641761296),
            LatLng(44.406884259837355,26.10205319374163),
            LatLng(44.40701628163416,26.101602582627127),
            LatLng(44.40701953908736,26.101203469794743),
            LatLng(44.406857242193084,26.10085585524448),
            LatLng(44.40664589154313,26.100654153257526),
            LatLng(44.40647746178287,26.10059836317905),
            LatLng(44.40642361849343, 26.100585489754483)

        )
    )

    circuitDao.insertCircuit(circuitTineretului){
        println("Circuit inserted: $it")
    }

}