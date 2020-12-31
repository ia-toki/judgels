import { APP_CONFIG, Mode } from '../../conf';

import './TimeanddateLink.css';

export function TimeanddateLink({ time, message, children }) {
  const onClick = e => {
    e.preventDefault();

    const date = new Date(time)
      .toISOString()
      .replace(/[-:Z.]/g, '')
      .substring(0, 15);

    window.open(`https://www.timeanddate.com/worldclock/fixedtime.html?msg=${encodeURIComponent(message)}&iso=${date}`);
  };

  if (APP_CONFIG.mode === Mode.PRIVATE_CONTESTS) {
    return children;
  }
  return (
    <span className="timeanddate" onClick={onClick}>
      {children}
    </span>
  );
}
