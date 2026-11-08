import { isTrainingEnabled } from '../../conf';

import './TimeanddateLink.scss';

export function TimeanddateLink({ time, message, children }) {
  const onClick = e => {
    e.preventDefault();

    const date = new Date(time)
      .toISOString()
      .replace(/[-:Z.]/g, '')
      .substring(0, 15);

    window.open(`https://www.timeanddate.com/worldclock/fixedtime.html?msg=${encodeURIComponent(message)}&iso=${date}`);
  };

  if (!isTrainingEnabled()) {
    return children;
  }
  return (
    <span className="timeanddate-link" onClick={onClick}>
      {children}
    </span>
  );
}
