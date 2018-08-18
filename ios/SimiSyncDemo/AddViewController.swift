//
//  AddViewController.swift
//  SimiSyncDemo
//
//  Created by Gordan Glavaš on 18/08/2018.
//  Copyright © 2018 Gordan Glavaš. All rights reserved.
//

import UIKit

class AddViewController: UIViewController {
    
    @IBOutlet weak var date: UIDatePicker!
    @IBOutlet weak var brand: UIButton!
    @IBOutlet weak var quantity: UITextField!
    
    private var brands: [JavaUtilMap]!
    private var brandGuid: String!

    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.brands = ConversionUtil.array(from: SMSimiMapper.fromArray(with: SMActiveSimi.eval(with: "BeerApp", with: "brands", withSMSimiPropertyArray: IOSObjectArray(length: 0, type: SMSimiProperty_class_())).getValue().getObject())) as! [JavaUtilMap]

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    // MARK: - Actions
    
    @IBAction func brandTap(_ sender: UIButton) {
        let ac = UIAlertController(title: "Brands", message: "Select beer brand", preferredStyle: .actionSheet)
        for brand in brands {
            ac.addAction(UIAlertAction(title: brand.getWithId("name") as? String, style: .default, handler: { (action) in
                self.brandGuid = brand.getWithId("guid") as! String
                self.brand.setTitle(action.title, for: .normal)
            }))
        }
        present(ac, animated: true, completion: nil)
    }
    
    @IBAction func saveTap(_ sender: Any) {
        let callback = NetCallback(success: { (response) in
            DispatchQueue.main.async {
                self.navigationController?.popViewController(animated: true)
            }
        }) { (response) in
            print(response)
        }!
        SMActiveSimi.eval(with: "BeerApp", with: "put", withSMSimiPropertyArray: IOSObjectArray(nsArray: [
            SMSimiValue_Number(double: self.date.date.timeIntervalSince1970),
            SMSimiValue_String(nsString: self.brandGuid),
            SMSimiValue_Number(double: Double(self.quantity.text!)!),
            SMSimiValue_String(nsString: UserDefaults.standard.string(forKey: "cookie")),
            callback.success,
            callback.error
            ], type: SMSimiProperty_class_()))
    }
}
