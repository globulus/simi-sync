//
//  LoginViewController.swift
//  SimiSyncDemo
//
//  Created by Gordan Glavaš on 18/08/2018.
//  Copyright © 2018 Gordan Glavaš. All rights reserved.
//

import UIKit

class LoginViewController: UIViewController {

    @IBOutlet weak var email: UITextField!
    @IBOutlet weak var password: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        UserDefaults.standard.removeObject(forKey: "cookie")

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

    @IBAction func goTap(_ sender: Any) {
        let loginCallback = NetCallback(success: { (response) in
            let cookie = response?.getString()
            UserDefaults.standard.set(cookie, forKey: "cookie")
            DispatchQueue.main.async {
                self.performSegue(withIdentifier: "segLogin2Beers", sender: self)
            }
        }) { (response) in
            print("Login error")
        }!
        DispatchQueue.global(qos: .background).async {
            SMActiveSimi.eval(with: "BeerApp", with: "login", withSMSimiPropertyArray: IOSObjectArray(nsArray: [SMSimiMapper.toSimiProperty(withId: self.email.text), SMSimiMapper.toSimiProperty(withId: self.password.text), loginCallback.success, loginCallback.error], type: SMSimiProperty_class_()))
        }
    }
}
