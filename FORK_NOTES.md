# Fork Notes

## Overview

This is a fork of the original [MyBrain](https://github.com/mhss1/MyBrain) productivity app, maintained and customized for personal development needs.

## Fork Origin

- **Original Repository**: [mhss1/MyBrain](https://github.com/mhss1/MyBrain)
- **Fork Maintainer**: [N-cryptd](https://github.com/N-cryptd)
- **Fork Created**: February 2026
- **Base Version**: Last successfully compiled version (commit 56b4251)

## Current Modifications

### AI Integration
- **GLM AI Provider**: Added support for GLM Coding Plan models
  - Default model: glm-4.7
  - Alternative models: glm-4.5, glm-4.6
  - API endpoint: https://api.z.ai/api/coding/paas/v4
  - Custom URL configuration support

### Repository Configuration
- All repository links updated to point to this fork (N-cryptd/MyBrain)
- Development roadmap created as GitHub Project
- Funding links removed
- Issue tracking configured for this fork

### Branch Strategy
- **master**: Production-ready, stable code
- **dev**: Development branch for new features and bug fixes
- All pull requests should target the `dev` branch

### CI/CD
- GitHub Actions workflow for automated APK builds
- Automated debug and release APK generation
- Artifact management for each build

## Development Roadmap

The fork follows a tier-based development approach:

### Tier 1: Core Stability
Priority focus on fixing existing issues and ensuring app stability:
- Fix Habits module compilation errors
- Fix NoteEntity transient properties
- Remove VoiceInputUtil and clean up references
- CI/CD improvements and build optimization
- Update and test GLM AI integration

### Tier 2: Enhanced Features
Expand functionality with new and improved features:
- Complete Smart Note Linking (bi-directional)
- Add AI-Powered Universal Search
- Improve AI Agent tools functionality
- Enhanced calendar widget features
- Advanced task management features

### Tier 3: Future Enhancements
Long-term feature development:
- Voice input functionality
- Cloud synchronization options
- Advanced analytics and reporting
- Enhanced mood tracking with AI insights
- Collaboration features

See the [GitHub Project Roadmap](https://github.com/users/N-cryptd/projects/2) for detailed tracking and progress.

## Differences from Original

### Removed Features
- Habits module (temporarily removed due to compilation errors, planned for restoration)
- VoiceInputUtil (removed due to compilation issues)

### New Features
- GLM AI provider integration
- Custom AI tool implementations
- Personalized development roadmap

### Modified Features
- Updated all repository links and references
- Enhanced build configuration
- Updated contribution guidelines

## Building the Fork

For build instructions, see [BUILD_INSTRUCTIONS.md](BUILD_INSTRUCTIONS.md).

Key differences from original build process:
- GLM API key required for AI functionality
- GitHub Actions recommended for building (due to ARM64 architecture limitations)
- Fork-specific build configurations

## Contributing

Contributions to this fork are welcome! Please refer to [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

### Guidelines for This Fork
- All pull requests should target the `dev` branch
- Follow the existing code style and architecture
- Test changes on actual Android devices
- Update documentation as needed

## Issues and Support

- Report bugs and feature requests via [GitHub Issues](https://github.com/N-cryptd/MyBrain/issues)
- Check the [Roadmap](https://github.com/users/N-cryptd/projects/2) for planned features
- Review existing issues before creating new ones

## License

This fork maintains the same GPL-3.0 license as the original project.
