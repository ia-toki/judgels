import { Fragment } from 'react';

import RichStatementText from '../RichStatementText/RichStatementText';
import { UserRef } from '../UserRef/UserRef';

import './ProblemEditorial.scss';

export function ProblemEditorial({ title, settersMap, profilesMap, children }) {
  const renderWriters = () => {
    const writerJids = settersMap.WRITER;
    if (!writerJids) {
      return null;
    }

    return (
      <li>
        Written by:&nbsp;
        {writerJids.map(jid => (
          <Fragment key={jid}>
            <UserRef profile={profilesMap[jid]} />
            &nbsp;
          </Fragment>
        ))}
      </li>
    );
  };

  const renderDevelopers = () => {
    const developerJids = settersMap.DEVELOPER;
    if (!developerJids) {
      return null;
    }

    return (
      <li>
        Developed by:&nbsp;
        {developerJids.map(jid => (
          <Fragment key={jid}>
            <UserRef profile={profilesMap[jid]} />
            &nbsp;
          </Fragment>
        ))}
      </li>
    );
  };

  const renderEditorialists = () => {
    const editorialistJids = settersMap.EDITORIALIST;
    if (!editorialistJids) {
      return null;
    }

    return (
      <li>
        Editorial by:&nbsp;
        {editorialistJids.map(jid => (
          <Fragment key={jid}>
            <UserRef profile={profilesMap[jid]} />
            &nbsp;
          </Fragment>
        ))}
      </li>
    );
  };

  return (
    <div className="problem-editorial">
      <h4>{title}</h4>
      <ul>
        {renderWriters()}
        {renderDevelopers()}
        {renderEditorialists()}
      </ul>
      <hr />
      <RichStatementText key={title}>{children}</RichStatementText>
    </div>
  );
}
