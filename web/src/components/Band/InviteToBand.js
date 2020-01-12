 import React from 'react';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form'
import fetcingFactory from "../../Utils/external";
import {endpoints} from "../../Utils/Types";
import Select from 'react-dropdown-select';
import { Radio } from 'antd';
import 'antd/dist/antd.css';

export default class InviteToBand extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            musicians: [],
            selectedBand: "",
            musicianName: "",
            bands: [],
            selectedMusician: "",
            isSearching: false,
            main: true
        }
        this.handleRadioChange = this.handleRadioChange.bind(this)
    }

    componentDidMount() {
        this.setState({isSearching: true})
        fetcingFactory(endpoints.GET_BANDS_LEAD, "").then(
            response => response.json()
            ).then(response => {
                console.log(response.code)
                if(response.code === 40001){
                    if(response.violationErrors[0].code === 40003) {
                        alert("You are not a leader of any bands")
                    }
                }
                else {
                    for(let i=0; i<response.length;i++) {
                        this.setState(prevState => ({
                            bands: [...prevState.bands, {value: response[i].id, label: response[i].name}]
                          }))
                    }
                    console.log(this.state.bands)
                }
            }).then( () => 
        this.setState({isSearching: false}))
    }

    setBandValues = selectedBand => {
        this.setState({ selectedBand }
        , () => console.log(this.state.selectedBand)
        )
    }

    setMusicianValues = selectedMusician => {
        this.setState({ selectedMusician}
        , () => console.log(this.state.selectedMusician)
        )
    }

    handleChange = event => {
        this.setState({
            [event.target.id]: event.target.value
        }
        );
    }

    handleGetMusician = event => {
        event.preventDefault();
        console.log(this.state.musicianName)
        this.setState({isSearching: true})
        fetcingFactory(endpoints.GET_MUSICIAN, JSON.stringify({"userName": this.state.musicianName})).then(
            response => response.json()
            ).then(response => {
                if (response.length === 0) {
                    alert("No musician with that name")
                }
                else {
                    //console.log(response)
                    this.setState({musicians: []})
                    for(let i = 0; i < response.length; i++) {
                        this.setState(prevState => ({
                            musicians: [...prevState.musicians, {value: response[i].id, label: response[i].username}]
                          }))
                    }
                    console.log(this.state.musicians)
                }   
            }).then( () => 
            this.setState({isSearching: false}))
    }

    handleSubmit = event => {
        event.preventDefault();
        let params = JSON.stringify({
            "bandId": this.state.selectedBand,
            "musicianId": this.state.selectedMusician
        });
        console.log(params)
        if(this.state.main === true){
        fetcingFactory(endpoints.INVITE_MAIN_MEMB, params).then(
            response => {
                if (response.status === 200) {
                    window.location.href = "/home";
                } else {
                    console.log(response)
                    alert(response.json())
                }
            });
        }
        else {
            fetcingFactory(endpoints.INVITE_BACKUP_MEMB, params).then(
                response => {
                    if (response.status === 200) {
                        window.location.href = "/home";
                    } else {
                        console.log(response)
                        alert(response.json())
                    }
                });
        }
    }

    handleRadioChange = e => {
        console.log('radio checked', e.target.value);
        this.setState({
          main: e.target.value,
        });
    }

    render () {
        return (
            <React.Fragment>
                <div className="modal-login">
                        <div className="modal-content">

                            <div className="modal-header">				
                                <h4 className="modal-title">Invite to band</h4>
                            </div>

                            <div className="modal-body">
                                <form onSubmit={this.handleSubmit}>
                                    <Select
                                        disabled={this.state.isSearching}
                                        name="selectedBand"
                                        options={this.state.bands}
                                        value={this.state.selectedBand}
                                        placeholder="Select band"
                                        //onChange={this.updateEventType}
                                        onChange={value => this.setBandValues(value[0].value)}
                                    />
                                    <br></br>
                                    <div className="form-group">
                                        <div className="input-group">
                                            <span className="input-group-addon"><i className="fa fa-user"></i></span>
                                            <input type="text" 
                                            onChange={this.handleChange}
                                            className="form-control" name="musicianName" placeholder="Musician name">
                                            </input>
                                        </div>
                                    </div>
                                    <Button type="button" block disabled={this.state.isSearching} onClick={this.handleGetMusician}> Get musicians </Button>
                                    <br></br>
                                    <Select
                                        disabled={this.state.isSearching}
                                        name="selectedMusician"
                                        options={this.state.musicians}
                                        value={this.state.selectedMusician}
                                        placeholder="Choose musician"
                                        //onChange={this.updateEventType}
                                        onChange={value => this.setMusicianValues(value[0].value)}
                                    />
                                    <br></br>
                                    <Radio.Group onChange={this.handleRadioChange} value={this.state.main}>
                                        <Radio value={true}>Main member</Radio>
                                        <Radio value={false}>Backup member</Radio>
                                    </Radio.Group>
                                    <br></br><br></br>
                                    <div className="form-group">
                                        <button type="submit" className="btn btn-primary btn-block btn-lg">Invite to band</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
            </React.Fragment>
        );
    }

}
