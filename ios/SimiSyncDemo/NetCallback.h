//
//  NetCallback.h
//  SimiSyncDemo
//
//  Created by Gordan Glavaš on 16/08/2018.
//  Copyright © 2018 Gordan Glavaš. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "SimiValue.h"
#import "SimiCallable.h"

@interface CallbackWithResponse : NSObject <SMSimiCallable>

@end

@interface NetCallback : NSObject

@property (strong, nonatomic, readonly) SMSimiValue_Callable *success;
@property (strong, nonatomic, readonly) SMSimiValue_Callable *error;

- (instancetype)initWithSuccess:(void (^)(SMSimiValue *))success
                          error:(void (^)(SMSimiValue *))error;

@end
