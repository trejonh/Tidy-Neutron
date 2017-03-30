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
class ViewController: UIViewController, UITableViewDelegate, UITableViewDataSource, CBCentralManagerDelegate{
    var centralManager: CBCentralManager?
    var peripherals = Array<CBPeripheral>()
    
    //MARK: Properties
    
    @IBOutlet weak var welcomeBanner: UILabel!
    @IBOutlet weak var blueToothTableView: UITableView!
    //MARK: Actions
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        //Init CoreBluetooth Central Manager
        centralManager = CBCentralManager(delegate: self, queue: nil)
        if(blueToothTableView != nil){
            blueToothTableView.delegate = self
            blueToothTableView.dataSource = self
        }
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
/*}

extension ViewController: CBCentralManagerDelegate{*/
    func centralManagerDidUpdateState(central: CBCentralManager) {
        if(central.state == .PoweredOn){
            self.centralManager?.scanForPeripheralsWithServices(nil, options: nil)
        }else{
            //alert user
            let alertController = UIAlertController(title: "Error", message:
                "No Bluetooth Devices Were Found", preferredStyle: UIAlertControllerStyle.Alert)
            
            AudioServicesPlayAlertSound(SystemSoundID(kSystemSoundID_Vibrate)) // vibration
            
            alertController.addAction(UIAlertAction(title: "Ok", style: UIAlertActionStyle.Default,handler: nil))
            self.presentViewController(alertController, animated: true, completion: nil)        }
    }
    func centralManager(_central: CBCentralManager, didDiscover peripheral: CBPeripheral, advertisementData: [String : Any], rssi RSSI: NSNumber) {
        peripherals.append(peripheral)
        blueToothTableView.reloadData()
    }
/*}

extension ViewController: UITableViewDataSource {*/
    func tableView(tableview: UITableView, cellForRowAtIndexPath: NSIndexPath) -> UITableViewCell {
        let cell:UITableViewCell = self.blueToothTableView.dequeueReusableCellWithIdentifier("cell")! as UITableViewCell
        
        let peripheral = peripherals[cellForRowAtIndexPath.row]
        cell.textLabel?.text = peripheral.name
        
        return cell
    }
    
    func tableView(tableview: UITableView, numberOfRowsInSection section: Int) -> Int {
        return peripherals.count
    }
    
    /*func numberOfSections(in tableView: UITableView) -> Int {
        return 1//self.blueToothTableView.
    }*/
}

