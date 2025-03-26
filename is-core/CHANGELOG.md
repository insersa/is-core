# Change Log

## [6.0.4] - First Open Source Release

### Added
- Added `FULL_LIKE` case to SQL query construction in `DAOTools`.

### Changed
- RD-115: Reduced vulnerabilities in version 6.0.4 of our libraries (`is-core` and `is_rest`) due to dependencies.
- Simplified `pom.xml` to use plugin values from `pom-project`.
- Upgraded Quartz to version `2.5.0`.
- Refactored CSV formatting in `AbstractFormatEngine` to use index-based iteration and remove unnecessary separators.

### Fixed
- RD-101: `AbstractDynamicDAO` did not support search with `ADDITIONAL_STATEMENT` and `OR` list.