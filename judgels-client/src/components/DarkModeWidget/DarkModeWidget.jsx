import { Alignment, Button, Navbar } from '@blueprintjs/core';
import { Flash, Moon } from '@blueprintjs/icons';

import { useWebPrefs } from '../../modules/webPrefs';

import './DarkModeWidget.scss';

export default function DarkModeWidget() {
  const { isDarkMode, setIsDarkMode } = useWebPrefs();

  const label = isDarkMode ? 'Switch to light mode' : 'Switch to dark mode';

  const widget = (
    <Button
      className="dark-mode-widget__button"
      variant="minimal"
      icon={isDarkMode ? <Moon /> : <Flash />}
      title={label}
      aria-label={label}
      onClick={() => setIsDarkMode(!isDarkMode)}
    />
  );

  return <Navbar.Group align={Alignment.RIGHT}>{widget}</Navbar.Group>;
}
