import * as classNames from 'classnames';
import * as React from 'react';
import FlagIcon from 'react-flag-kit/lib/FlagIcon';
import { Link } from 'react-router-dom';

import { Profile } from 'modules/api/jophiel/profile';
import { getRatingClass } from 'modules/api/jophiel/userRating';

import './UserRef.css';

export interface UserRefProps {
  profile: Profile;
  showFlag?: boolean;
}

export class UserRef extends React.PureComponent<UserRefProps> {
  render() {
    return (
      <>
        {this.renderFlag()}
        {this.renderUsername()}
      </>
    );
  }

  private renderFlag = () => {
    const { profile, showFlag } = this.props;
    if (!showFlag) {
      return null;
    }
    if (!profile.country) {
      return <div className="user-ref__flag-dummy" />;
    }
    return <FlagIcon code={profile.country} size={20} className="user-ref__flag" />;
  };

  private renderUsername = () => {
    const { profile } = this.props;
    return (
      <Link
        className={classNames('user-ref__username', getRatingClass(profile.rating))}
        to={`/profiles/${profile.username}`}
      >
        {profile.username}
      </Link>
    );
  };
}
