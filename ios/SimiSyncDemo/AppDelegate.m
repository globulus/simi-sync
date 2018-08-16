//
//  AppDelegate.m
//  SimiCalc
//
//  Created by Gordan Glavaš on 20/04/2018.
//  Copyright © 2018 Gordan Glavaš. All rights reserved.
//

#import "AppDelegate.h"

#import "SimiProperty.h"
#import "SimiMapper.h"
#import "SimiValue.h"
#import "NetCallback.h"
#import "java/util/List.h"
#import "java/lang/Double.h"

@interface AppDelegate ()

@end

@implementation AppDelegate


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    // Override point for customization after application launch.
    
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
    
    NSString *dir = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES)[0];
    [SMActiveSimi setImportResolverWithSMActiveSimi_ImportResolver:self];
    [SMActiveSimi load__WithNSStringArray:[IOSObjectArray arrayWithNSArray:@[@"SimiSync.simi"] type:[IOSClass classForIosName:@"NSString"]]];
    [SMActiveSimi evalWithNSString:@"SimiSync"
                      withNSString:@"configure"
           withSMSimiPropertyArray:[IOSObjectArray arrayWithNSArray:@[[SMSimiMapper toSimiPropertyWithId:@"http://localhost:8888"], [SMSimiMapper toSimiPropertyWithId:[[JavaLangDouble alloc] initWithDouble:1]]] type:SMSimiProperty_class_()]];
    NetCallback *callback = [[NetCallback alloc] initWithSuccess:^(SMSimiValue *response) {
        id<JavaUtilList> list = [SMSimiMapper fromArrayWithSMSimiObject:response.getObject];
        [SMActiveSimi load__WithNSStringArray:[list toArray]];
        [SMActiveSimi load__WithNSStringArray:[IOSObjectArray arrayWithNSArray:@[@"BeerApp.simi"] type:[IOSClass classForIosName:@"NSString"]]];
        
        NetCallback *loginCallback = [[NetCallback alloc] initWithSuccess:^(SMSimiValue *response) {
            
            NSLog(@"Success");
        } error:^(SMSimiValue *response) {
            
            NSLog(@"Error");
        }];
        [SMActiveSimi evalWithNSString:@"BeerApp"
                          withNSString:@"login"
               withSMSimiPropertyArray:[IOSObjectArray arrayWithNSArray:@[[SMSimiMapper toSimiPropertyWithId:@"a@a.com"], [SMSimiMapper toSimiPropertyWithId:@"passs"], loginCallback.success, loginCallback.error] type:SMSimiProperty_class_()]];
    } error:^(SMSimiValue *response) {
        NSLog(@"Error");
    }];
    [SMActiveSimi evalWithNSString:@"SimiSync"
                      withNSString:@"sync"
           withSMSimiPropertyArray:[IOSObjectArray arrayWithNSArray:@[[SMSimiMapper toSimiPropertyWithId:dir], callback.success, callback.error] type:SMSimiProperty_class_()]];
    return YES;
}


- (void)applicationWillResignActive:(UIApplication *)application {
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
}


- (void)applicationDidEnterBackground:(UIApplication *)application {
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}


- (void)applicationWillEnterForeground:(UIApplication *)application {
    // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
}


- (void)applicationDidBecomeActive:(UIApplication *)application {
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}


- (void)applicationWillTerminate:(UIApplication *)application {
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}

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
