
import React, { Component } from 'react';
import { requireNativeComponent } from 'react-native';
import PropTypes from 'prop-types';

const MjpegPlayer = requireNativeComponent(`MjpegPlayer`, RNMjpegplayer);

export default class RNMjpegplayer extends Component {
  render() {
    return <MjpegPlayer {...this.props} />;
  }
}

RNMjpegplayer.propTypes = {
  settings: PropTypes.shape({}).isRequired,
};
