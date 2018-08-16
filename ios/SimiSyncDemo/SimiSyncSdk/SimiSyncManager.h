//
//  SimiSyncManager.h
//  SimiSyncDemo
//
//  Created by Gordan Glavaš on 16/08/2018.
//  Copyright © 2018 Gordan Glavaš. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "ActiveSimi.h"
#import "NetCallback.h"

@interface SimiSyncManager : NSObject <SMActiveSimi_ImportResolver>

+ (instancetype)sharedInstance;

- (void)bootWithBaseUrl:(NSString *)baseUrl
                version:(int)version
               callback:(NetCallback *)callback;

@end
