//
//  AppDelegate.h
//  SimiCalc
//
//  Created by Gordan Glavaš on 20/04/2018.
//  Copyright © 2018 Gordan Glavaš. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ActiveSimi.h"
#import "IOSClass.h"

@interface AppDelegate : UIResponder <UIApplicationDelegate, SMActiveSimi_ImportResolver>

@property (strong, nonatomic) UIWindow *window;


@end

