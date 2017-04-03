//
//  ViewController.swift
//  Tidy-Neturon
//
//  Created by Trejon House on 3/26/17.
//  Copyright © 2017 TreJon House. All rights reserved.
//

import UIKit
import CoreBluetooth
import AudioToolbox
class ViewController: UIViewController{
    //MARK: Properties
    
    @IBOutlet weak var welcomeBanner: UILabel!
    //MARK: Actions
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    //MARK: Actions
    
    @IBAction func connectToDevice(sender: UIButton) {
        welcomeBanner.text = "connect to device"
    }
    
    @IBAction func changeDeviceConfig(sender: UIButton) {
        welcomeBanner.text = "change device config"
    }
}

