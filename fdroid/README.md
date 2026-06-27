# F-Droid Build Metadata

This directory contains the F-Droid build metadata file ready for submission to the [fdroiddata](https://gitlab.com/fdroid/fdroiddata) repository.

## Submitting

1. Fork [fdroiddata](https://gitlab.com/fdroid/fdroiddata) on GitLab.
2. Clone your fork and create a branch named `uk.ewancroft.inkwell`.
3. Copy `uk.ewancroft.inkwell.yml` to `metadata/uk.ewancroft.inkwell.yml` in your fdroiddata clone.
4. Test the metadata:

   ```bash
   fdroid readmeta
   fdroid rewritemeta uk.ewancroft.inkwell
   fdroid lint uk.ewancroft.inkwell
   fdroid build uk.ewancroft.inkwell
   ```

5. Commit and push:

   ```bash
   git add metadata/uk.ewancroft.inkwell.yml
   git commit -m "New App: uk.ewancroft.inkwell"
   git push origin uk.ewancroft.inkwell
   ```

6. Open a merge request against fdroiddata on GitLab.

## Prerequisites

- Git tag `v1.0.0` must exist in the inkwell-android repo.
- Fastlane metadata must be present in `fastlane/metadata/android/en-US/` (already in this repo).
- All dependencies must be FOSS (no Firebase, GMS, or other proprietary libraries).
