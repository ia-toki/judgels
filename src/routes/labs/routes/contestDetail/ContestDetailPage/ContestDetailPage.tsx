import * as React from 'react';
import { withBreadcrumb } from '../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { DetailPage, DetailPageProps } from '../../../../../components/DetailsPage/DetailPage';
import { Card } from '../../../../../components/Card/Card';
import { DetailPageSide } from '../../../../../components/DetailsPage/DetailPageSide/DetailPageSide';
import { loremIpsum } from './mockContestDetail';
import { HorizontalDivider } from '../../../../../components/HorizontalDivider/HorizontalDivider';
import { ContestRegistrantTable } from '../ContestRegistrantTable/ContestRegistrantTable';
import { mockContestRegistrant } from '../../../../../modules/api/uriel/registrant';

export interface ContestDetailPageProps {
  className?: string;
  name: string;
  description?: string;
}

class ContestDetailPage extends React.Component<ContestDetailPageProps> {
  render() {
    var mockContestDetail: DetailPageProps = {
      name: 'TOKI Open Contest April 2017',
      description: 'Thursday, April 2 17:00-21:00 WIB | 2-Divisions Contest',
      mainContent: [
        Card({
          title: 'Contest Overview',
          children: <p>{loremIpsum}</p>,
        }),
        HorizontalDivider(),
        ContestRegistrantTable({ data: mockContestRegistrant }),
      ],
      sideContent: DetailPageSide(),
    };

    return <DetailPage {...mockContestDetail} />;
  }
}

export default withBreadcrumb('Contest Details Page')(ContestDetailPage);
