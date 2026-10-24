import { FormGroup } from '@blueprintjs/core';
import classNames from 'classnames';
import AceEditor from 'react-ace';

import 'ace-builds/src-noconflict/ext-language_tools';
import 'ace-builds/src-noconflict/mode-c_cpp';
import 'ace-builds/src-noconflict/mode-html';
import 'ace-builds/src-noconflict/mode-plain_text';
import 'ace-builds/src-noconflict/theme-tomorrow';
import 'ace-builds/src-noconflict/theme-tomorrow_night';

import { getGradingLanguageFamily } from '../../../modules/api/gradingLanguage';
import { useWebPrefs } from '../../../modules/webPrefs';
import { FormInputValidation } from '../FormInputValidation/FormInputValidation';
import { getIntent } from '../meta';

import './FormAceEditor.scss';

export default function FormAceEditor({ input, meta, autoFocus, gradingLanguage, mode, tall }) {
  const { isDarkMode } = useWebPrefs();

  return (
    <FormGroup
      intent={getIntent(meta)}
      className={classNames('form-group-editor', { 'form-group-editor--tall': tall })}
    >
      <AceEditor
        mode={mode || (getGradingLanguageFamily(gradingLanguage) === 'C++' ? 'c_cpp' : 'plain_text')}
        theme={isDarkMode ? 'tomorrow_night' : 'tomorrow'}
        width="100%"
        height="100%"
        fontSize={14}
        showPrintMargin={false}
        editorProps={{ $blockScrolling: true }}
        setOptions={{ useWorker: false }}
        name={input.name}
        onChange={input.onChange}
        onFocus={input.onFocus}
        value={input.value}
        focus={autoFocus}
      />
      <FormInputValidation meta={meta} />
    </FormGroup>
  );
}
