import { Navbar, Switch, Alignment } from '@blueprintjs/core';
import { Moon } from '@blueprintjs/icons';
import { connect } from 'react-redux';

import { selectIsDarkMode } from '../../modules/webPrefs/webPrefsSelectors';
import * as webPrefsActions from '../../modules/webPrefs/webPrefsActions';

import './DarkModeWidget.scss';

function DarkModeWidget({ isDarkMode, onChangeDarkMode }) {
  const changeDarkMode = ({ target }) => {
    onChangeDarkMode(target.checked);
  };

  const widget = (
    <Switch
      className="dark-mode-widget__switch"
      alignIndicator={Alignment.RIGHT}
      label={<Moon />}
      checked={isDarkMode}
      onChange={changeDarkMode}
    />
  );

  return <Navbar.Group align={Alignment.RIGHT}>{widget}</Navbar.Group>;
}

const mapStateToProps = state => ({
  isDarkMode: selectIsDarkMode(state),
});

const mapDispatchToProps = {
  onChangeDarkMode: webPrefsActions.switchDarkMode,
};

export default connect(mapStateToProps, mapDispatchToProps)(DarkModeWidget);
