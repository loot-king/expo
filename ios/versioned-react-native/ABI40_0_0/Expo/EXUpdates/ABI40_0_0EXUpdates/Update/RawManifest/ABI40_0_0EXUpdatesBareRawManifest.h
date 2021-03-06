//  Copyright © 2021 650 Industries. All rights reserved.

#import <ABI40_0_0EXUpdates/ABI40_0_0EXUpdatesRawManifest.h>
#import <ABI40_0_0EXUpdates/ABI40_0_0EXUpdatesBaseLegacyRawManifest.h>

NS_ASSUME_NONNULL_BEGIN

@interface ABI40_0_0EXUpdatesBareRawManifest : ABI40_0_0EXUpdatesBaseLegacyRawManifest<ABI40_0_0EXUpdatesRawManifestBehavior>

- (NSNumber *)commitTimeNumber;
- (nullable NSDictionary *)metadata;
- (nullable NSArray *)assets;

@end

NS_ASSUME_NONNULL_END
