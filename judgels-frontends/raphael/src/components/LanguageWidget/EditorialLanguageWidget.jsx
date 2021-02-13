import { connect } from 'react-redux';

import { selectEditorialLanguage } from '../../modules/webPrefs/webPrefsSelectors';
import { sortLanguagesByName } from '../../modules/api/sandalphon/language';
import LanguageForm from './LanguageForm/LanguageForm';
import * as webPrefsActions from '../../modules/webPrefs/webPrefsActions';

import './LanguageWidget.css';

function EditorialLanguageWidget({ defaultLanguage, editorialLanguages, editorialLanguage, onChangeLanguage }) {
  let initialLanguage;
  if (editorialLanguages.indexOf(editorialLanguage) !== -1) {
    initialLanguage = editorialLanguage;
  } else {
    initialLanguage = defaultLanguage;
  }
  const formProps = {
    form: 'editorial-language-form',
    languages: sortLanguagesByName(editorialLanguages),
    initialValues: {
      language: initialLanguage,
    },
  };

  return (
    <div className="language-widget">
      <div className="language-widget__right">
        <LanguageForm onSubmit={onChangeLanguage} {...formProps} />
      </div>
      <div className="clearfix" />
    </div>
  );
}

const mapStateToProps = state => ({
  editorialLanguage: selectEditorialLanguage(state),
});

const mapDispatchToProps = {
  onChangeLanguage: data => webPrefsActions.switchEditorialLanguage(data.language),
};

export default connect(mapStateToProps, mapDispatchToProps)(EditorialLanguageWidget);
