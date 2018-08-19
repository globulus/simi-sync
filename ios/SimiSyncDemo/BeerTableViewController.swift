//
//  BeerTableViewController.swift
//  SimiSyncDemo
//
//  Created by Gordan Glavaš on 18/08/2018.
//  Copyright © 2018 Gordan Glavaš. All rights reserved.
//

import UIKit

class BeerTableViewController: UITableViewController {
    
    private var items = [JavaUtilMap]()
    private var brands = [String: String]()
    private let dateFormatter = DateFormatter()

    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.refreshControl = UIRefreshControl()
        self.refreshControl?.addTarget(self, action: #selector(refresh(_:)), for: .valueChanged)
        
        self.dateFormatter.dateFormat = "MM/dd, HH:mm"

        // Uncomment the following line to preserve selection between presentations
        // self.clearsSelectionOnViewWillAppear = false

        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        // self.navigationItem.rightBarButtonItem = self.editButtonItem
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        refresh(nil)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.items.count
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "BeerCell", for: indexPath)
        let map = self.items[indexPath.row]
        cell.textLabel?.text = "\(brand(forGuid: map.getWithId("brand") as! String)) x \(map.getWithId("quantity") as! Double)"
        let timestamp = (map.getWithId("date") as! JavaUtilMap).getWithId("timestamp") as! Int64
        cell.detailTextLabel?.text = self.dateFormatter.string(from: Date(timeIntervalSince1970: Double(timestamp) / 1000))
        return cell
    }

    override func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        return true
    }
    
    override func tableView(_ tableView: UITableView, editingStyleForRowAt indexPath: IndexPath) -> UITableViewCellEditingStyle {
        return .delete
    }
    
    override func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            let ac = UIAlertController(title: "Delete", message: "Delete this item?", preferredStyle: .alert)
            ac.addAction(UIAlertAction(title: "Yes", style: .destructive, handler: { (action) in
                let callback = NetCallback(success: { (response) in
                    self.items.remove(at: indexPath.row)
                    DispatchQueue.main.async {
                        self.tableView.deleteRows(at: [indexPath], with: .right)
                    }
                }, error: { (response) in
                    print(response)
                })!
                SMActiveSimi.eval(with: "BeerApp", with: "delete", withSMSimiPropertyArray: IOSObjectArray(nsArray: [SMSimiValue_String(nsString: self.items[indexPath.row].getWithId("guid") as! String), SMSimiValue_String(nsString: UserDefaults.standard.string(forKey: "cookie")), callback.success, callback.error], type: SMSimiProperty_class_()))
            }))
            ac.addAction(UIAlertAction(title: "No", style: .cancel, handler: nil))
            self.present(ac, animated: true, completion: nil)
        }
    }
    
    // MARK: - Actions
    
    @objc private func refresh(_ sender: UIRefreshControl?) {
        let callback = NetCallback(success: { (response) in
            self.items = ConversionUtil.array(from: SMSimiMapper.fromArray(with: response?.getValue().getObject())) as! [JavaUtilMap]
            DispatchQueue.main.async {
                self.tableView.reloadData()
                self.refreshControl?.endRefreshing()
            }
        }) { (response) in
            print(response)
        }!
        SMActiveSimi.eval(with: "BeerApp", with: "get", withSMSimiPropertyArray: IOSObjectArray(nsArray: [SMSimiValue_String(nsString: UserDefaults.standard.string(forKey: "cookie")), callback.success, callback.error], type: SMSimiProperty_class_()))
    }

    // MARK: - Other
    
    private func brand(forGuid guid: String) -> String {
        if let name = self.brands[guid] {
            return name
        }
        let name = SMActiveSimi.eval(with: "BeerApp", with: "brandForGuid", withSMSimiPropertyArray: IOSObjectArray(nsArray: [SMSimiValue_String(nsString: guid)], type: SMSimiProperty_class_())).getValue().getString()!
        self.brands[guid] = name
        return name
    }
}
