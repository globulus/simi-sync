//
//  NetCallback.m
//  SimiSyncDemo
//
//  Created by Gordan Glavaš on 16/08/2018.
//  Copyright © 2018 Gordan Glavaš. All rights reserved.
//

#import "NetCallback.h"

#import "java/util/List.h"

@interface NetCallback ()

@end

@interface CallbackWithResponse ()

@property (strong, nonatomic) void (^callback)(SMSimiValue *response);

- (instancetype)initWithCallback:(void (^)(SMSimiValue *))callback;

@end

@implementation NetCallback

- (instancetype)initWithSuccess:(void (^)(SMSimiValue *))success
                          error:(void (^)(SMSimiValue *))error {
    if (self = [super init]) {
        _success = new_SMSimiValue_Callable_initWithSMSimiCallable_withNSString_withSMSimiObject_([[CallbackWithResponse alloc] initWithCallback:success], @"success", nil);
        _error = new_SMSimiValue_Callable_initWithSMSimiCallable_withNSString_withSMSimiObject_([[CallbackWithResponse alloc] initWithCallback:error], @"error", nil);
    }
    return self;
}

@end

@implementation CallbackWithResponse

- (instancetype)initWithCallback:(void (^)(SMSimiValue *))callback {
    if (self = [super init]) {
        self.callback = callback;
    }
    return self;
}

- (id<SMSimiProperty>)callWithSMBlockInterpreter:(id<SMBlockInterpreter>)interpreter
                           withSMSimiEnvironment:(id<SMSimiEnvironment>)environment
                                withJavaUtilList:(id<JavaUtilList>)arguments
                                     withBoolean:(jboolean)rethrow {
    self.callback([[arguments getWithInt:0] getValue]);
    return nil;
}

- (jint)arity {
    return 1;
}

- (NSString *)toCodeWithInt:(jint)indentationLevel withBoolean:(jboolean)ignoreFirst {
    return nil;
}

- (NSString *)getFileName {
    return nil;
}

- (jboolean)hasBreakPoint {
    return false;
}

- (jint)getLineNumber {
    return 0;
}

@end
