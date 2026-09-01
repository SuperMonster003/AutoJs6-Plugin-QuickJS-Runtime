<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="{{ icon_url }}" alt="{{ icon_alt }}" border="0" width="128" />
  </p>

  <p>{{ text_plugin_synopsis }}</p>

  <p>
    <a href="{{ repo_url }}/releases"><img alt="GitHub release" src="https://img.shields.io/github/v/release/{{ repo_slug }}?label=Release"/></a>
    <a href="{{ repo_url }}/issues"><img alt="GitHub issues" src="https://img.shields.io/github/issues/{{ repo_slug }}?color=A24232&label=Issues"/></a>
    <a href="{{ license_url }}"><img alt="GitHub license" src="https://img.shields.io/github/license/{{ repo_slug }}?color=534BAE&label=License"/></a>
  </p>
</div>

******

### {{ h3_languages }}

******

{{ p_languages_all_supported }}:

{{ placeholder_ul_languages_all_supported }}

******

### {{ h3_introduction }}

******

{{ p_introduction }}

******

### {{ h3_usage }}

******

{{ placeholder_usage_steps }}

> {{ p_usage_note }}

******

### {{ h3_quick_start }}

******

{{ p_quick_start_intro }}:

```javascript
"quickjs";

console.log("QuickJS {{ quickjs_version }}");
console.log("Unicode", "\u4e2d\u6587", 2n ** 63n);
```

{{ p_quick_start_result }}

******

### {{ h3_compatibility }}

******

{{ placeholder_compatibility_points }}

******

### {{ h3_security }}

******

{{ placeholder_security_points }}

******

### {{ h3_plugin_interface }}

******

{{ p_plugin_interface }}:

```text
application id: {{ plugin_application_id }}
plugin id: {{ plugin_id }}
engine: {{ plugin_engine }}
variant: {{ plugin_variant }}
service action: {{ plugin_service_action }}
service category: {{ plugin_service_category }}
aidl interface: {{ plugin_aidl_interface }}
contract version: {{ plugin_contract_version }}
aidl methods: getInfo(), getRuntimeInfo(), runScript(request, source, callback), cancelScript(executionId), prewarmRuntime()
minimum host build: {{ required_host_version_code }} ({{ required_host_version_name }})
source limit: {{ max_source_size }}
combined stdout/stderr budget: {{ max_output_size }}
default timeout: {{ default_timeout }}
default memory limit: {{ default_memory_limit }}
maximum memory limit: {{ maximum_memory_limit }}
default stack limit: {{ default_stack_limit }}
```

{{ p_contract }}

{{ p_16kb_status }}

******

### {{ h3_release_history }}

******

{{ placeholder_latest_release_history }}

##### {{ h5_more_release_history }}

- {{ placeholder_read_more_in_changelog_md }}

******

### {{ h3_build }}

******

{{ p_build_intro }}

```powershell
py -B -m unittest discover -s tools\quickjs\tests -p "test_*.py"
py .python\generate_markdown.py --check
.\gradlew.bat testDebugUnitTest assembleDebug assembleDebugAndroidTest lintDebug
py tools\quickjs\verify_apk_variants.py app\build\outputs\apk debug
```

******

### {{ h3_localization }}

******

{{ p_localization }}

```text
.readme/common.json
.readme/lang_*.json
.readme/template_readme.md
.readme/template_plugin_instruction.md
.changelog/lang_*.json
.changelog/template_changelog.md
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values*/strings.xml
app/src/main/res/raw*/plugin_instruction.md
```

******

### {{ h3_license }}

******

{{ p_license }}

******

### {{ h3_links }}

******

- {{ text_link_autojs6 }}: {{ autojs6_url }}
- {{ text_link_quickjs }}: {{ quickjs_official_url }}
- {{ text_link_quickjs_docs }}: {{ quickjs_documentation_url }}
- {{ text_link_quickjs_source }}: {{ quickjs_repository_url }}
- {{ text_link_third_party_notices }}: {{ third_party_notices_url }}
