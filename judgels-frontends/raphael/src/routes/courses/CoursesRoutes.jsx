import { PredictiveAnalysis } from '@blueprintjs/icons';
import { Component } from 'react';
import { connect } from 'react-redux';
import { Route } from 'react-router';

import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import ContentWithSidebar from '../../components/ContentWithSidebar/ContentWithSidebar';
import CoursesPage from './courses/CoursesPage/CoursesPage';
import * as curriculumActions from './modules/curriculumActions';

class CoursesRoutes extends Component {
  state = {
    title: '',
  };

  async componentDidMount() {
    const curriculum = await this.props.onGetCurriculum();
    this.setState({ title: curriculum.name });
  }

  render() {
    const sidebarItems = [
      {
        id: '@',
        titleIcon: <PredictiveAnalysis />,
        title: this.state.title,
        routeComponent: Route,
        component: CoursesPage,
      },
    ];

    const contentWithSidebarProps = {
      title: 'Curriculums',
      items: sidebarItems,
    };

    return (
      <FullPageLayout>
        <ContentWithSidebar {...contentWithSidebarProps} />
      </FullPageLayout>
    );
  }
}

const mapDispatchToProps = {
  onGetCurriculum: curriculumActions.getCurriculum,
};

export default connect(undefined, mapDispatchToProps)(CoursesRoutes);
