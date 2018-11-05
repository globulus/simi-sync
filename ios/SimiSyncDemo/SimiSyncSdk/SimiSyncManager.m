//
//  SimiSyncManager.m
//  SimiSyncDemo
//
//  Created by Gordan Glavaš on 16/08/2018.
//  Copyright © 2018 Gordan Glavaš. All rights reserved.
//

#import "SimiSyncManager.h"

#import "IOSClass.h"
#import "SimiMapper.h"
#import "java/lang/Double.h"
#import "java/lang/Long.h"
#import "java/util/List.h"
#import "IOSDebugger.h"

@implementation SimiSyncManager

+ (instancetype)sharedInstance {
    static SimiSyncManager *sharedInstance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [[SimiSyncManager alloc] init];
    });
    return sharedInstance;
}

- (void)bootWithBaseUrl:(NSString *)baseUrl version:(int)version callback:(NetCallback *)callback {
    
    /*
     If you run into issues with number equality, go to J2ObjC_source.h and change cast_chk to the following:
     
     __attribute__ ((unused)) static inline id cast_chk(id __unsafe_unretained p, Class clazz) {
     #if !defined(J2OBJC_DISABLE_CAST_CHECKS)
     if (__builtin_expect(p && !([p isKindOfClass:clazz] || [NSStringFromClass([p class]) isEqualToString:NSStringFromClass(clazz)]), 0)) {
     JreThrowClassCastException(p, clazz);
     }
     #endif
     return p;
     }
     
     The reason this is happening is because of multiple libiOS.a causing SMSimiValue_Number classes to be different,
     hence isKindOfClass: calls failing. The above adds the check for equality of class names as well.
     */
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        NSString *dir = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES)[0];
        [SMActiveSimi setImportResolverWithSMActiveSimi_ImportResolver:self];
        [SMActiveSimi setDebugModeWithBoolean:true withSMDebugger_DebuggerInterface:[IOSDebugger sharedInstance]];
        [SMActiveSimi load__WithNSStringArray:[IOSObjectArray arrayWithNSArray:@[@"SimiSync.simi"] type:[IOSClass classForIosName:@"NSString"]]];
        [SMActiveSimi evalWithNSString:@"SimiSync"
                          withNSString:@"configure"
               withSMSimiPropertyArray:[IOSObjectArray arrayWithNSArray:@[[SMSimiMapper toSimiPropertyWithId:baseUrl], [SMSimiMapper toSimiPropertyWithId:[[JavaLangLong alloc] initWithLong:version]]] type:SMSimiProperty_class_()]];
        NetCallback *syncCallback = [[NetCallback alloc] initWithSuccess:^(SMSimiValue *response) {
            id<JavaUtilList> list = [SMSimiMapper fromArrayWithSMSimiObject:response.getObject];
            [SMActiveSimi load__WithNSStringArray:[list toArray]];
            [[callback.success getCallable] callWithSMBlockInterpreter:nil withSMSimiEnvironment:nil withJavaUtilList:nil withBoolean:true];
        } error:^(SMSimiValue *response) {
            NSLog(@"Error");
        }];
        [SMActiveSimi evalWithNSString:@"SimiSync"
                          withNSString:@"sync"
               withSMSimiPropertyArray:[IOSObjectArray arrayWithNSArray:@[[SMSimiMapper toSimiPropertyWithId:dir], syncCallback.success, callback.error] type:SMSimiProperty_class_()]];
    });
}

#pragma mark - SMActiveSimi_ImportResolver

- (NSString *)readFileWithNSString:(NSString *)fileName {
    NSString *path;
    if ([fileName hasPrefix:@"/"]) {
        path = fileName;
    } else {
        NSString *lastPart = [[fileName componentsSeparatedByString:@"/"] lastObject];
        NSArray *comps = [lastPart componentsSeparatedByString:@"."];
        path = [[NSBundle mainBundle] pathForResource:comps[0] ofType:comps[1]];
    }
    NSString *content = [NSString stringWithContentsOfFile:path encoding:NSUTF8StringEncoding error:nil];
    return content;
}

- (JavaNetURL *)resolveWithNSString:(NSString *)nativeFileName {
    return nil;
}

- (jboolean)useApiClassNameWithNSString:(NSString *)nativeFileName {
    return true;
}

@end
