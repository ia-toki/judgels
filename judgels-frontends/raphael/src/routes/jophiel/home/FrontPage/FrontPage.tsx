import * as React from 'react';
import { connect } from 'react-redux';

import { HomePage } from '../HomePage/HomePage';
import { WelcomePage } from '../WelcomePage/WelcomePage';
import { AppState } from '../../../../modules/store';
import { selectIsLoggedIn } from '../../../../modules/session/sessionSelectors';

interface FrontPageProps {
  isLoggedIn: boolean;
}

const FrontPage = (props: FrontPageProps) => {
  if (props.isLoggedIn) {
    return <HomePage />;
  } else {
    return <WelcomePage />;
  }
};

const mapStateToProps = (state: AppState) => ({
  isLoggedIn: selectIsLoggedIn(state),
});

export default connect(mapStateToProps)(FrontPage);
