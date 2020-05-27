\c chatbot

--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.15
-- Dumped by pg_dump version 9.6.15

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: chatbot; Type: TABLE; Schema: public; Owner: chatbot
--

CREATE TABLE public.chatbot (
    bot_id numeric NOT NULL,
    name character varying(100) NOT NULL,
    creation_date date NOT NULL,
    status character varying(100) NOT NULL,
    fil_id_avatar numeric,
    utt_id_welcome numeric NOT NULL,
    utt_id_default numeric NOT NULL,
    description text
);


ALTER TABLE public.chatbot OWNER TO chatbot;

--
-- Name: COLUMN chatbot.bot_id; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.chatbot.bot_id IS 'ID';


--
-- Name: COLUMN chatbot.name; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.chatbot.name IS 'Name';


--
-- Name: COLUMN chatbot.creation_date; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.chatbot.creation_date IS 'Creation date';


--
-- Name: COLUMN chatbot.status; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.chatbot.status IS 'Status';


--
-- Name: COLUMN chatbot.fil_id_avatar; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.chatbot.fil_id_avatar IS 'Avatar';


--
-- Name: COLUMN chatbot.utt_id_welcome; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.chatbot.utt_id_welcome IS 'Welcome text';


--
-- Name: COLUMN chatbot.utt_id_default; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.chatbot.utt_id_default IS 'Default text';


--
-- Name: COLUMN chatbot.description; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.chatbot.description IS 'Description';


--
-- Name: chatbot_node; Type: TABLE; Schema: public; Owner: chatbot
--

CREATE TABLE public.chatbot_node (
    nod_id numeric NOT NULL,
    url text NOT NULL,
    is_dev boolean NOT NULL,
    bot_id numeric NOT NULL,
    tra_id numeric,
    color character varying(20),
    name character varying(100),
    api_key character varying(100)
);


ALTER TABLE public.chatbot_node OWNER TO chatbot;

--
-- Name: COLUMN chatbot_node.nod_id; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.chatbot_node.nod_id IS 'ID';


--
-- Name: COLUMN chatbot_node.url; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.chatbot_node.url IS 'URL';


--
-- Name: COLUMN chatbot_node.is_dev; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.chatbot_node.is_dev IS 'Dev node';


--
-- Name: COLUMN chatbot_node.bot_id; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.chatbot_node.bot_id IS 'Chatbot';


--
-- Name: COLUMN chatbot_node.tra_id; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.chatbot_node.tra_id IS 'Loaded model';


--
-- Name: COLUMN chatbot_node.color; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.chatbot_node.color IS 'Color';


--
-- Name: groups; Type: TABLE; Schema: public; Owner: chatbot
--

CREATE TABLE public.groups (
    grp_id numeric NOT NULL,
    name character varying(100)
);


ALTER TABLE public.groups OWNER TO chatbot;

--
-- Name: COLUMN groups.grp_id; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.groups.grp_id IS 'Id';


--
-- Name: COLUMN groups.name; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.groups.name IS 'Name';


--
-- Name: media_file_info; Type: TABLE; Schema: public; Owner: chatbot
--

CREATE TABLE public.media_file_info (
    fil_id numeric NOT NULL,
    file_name character varying(100) NOT NULL,
    mime_type character varying(100) NOT NULL,
    length numeric NOT NULL,
    last_modified timestamp without time zone NOT NULL,
    file_path character varying(500),
    file_data bytea
);


ALTER TABLE public.media_file_info OWNER TO chatbot;

--
-- Name: COLUMN media_file_info.fil_id; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.media_file_info.fil_id IS 'Id';


--
-- Name: COLUMN media_file_info.file_name; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.media_file_info.file_name IS 'Name';


--
-- Name: COLUMN media_file_info.mime_type; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.media_file_info.mime_type IS 'MimeType';


--
-- Name: COLUMN media_file_info.length; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.media_file_info.length IS 'Size';


--
-- Name: COLUMN media_file_info.last_modified; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.media_file_info.last_modified IS 'Modification Date';


--
-- Name: COLUMN media_file_info.file_path; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.media_file_info.file_path IS 'path';


--
-- Name: COLUMN media_file_info.file_data; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.media_file_info.file_data IS 'data';


--
-- Name: nlu_training_sentence; Type: TABLE; Schema: public; Owner: chatbot
--

CREATE TABLE public.nlu_training_sentence (
    nts_id numeric NOT NULL,
    text character varying(100) NOT NULL,
    smt_id numeric NOT NULL
);


ALTER TABLE public.nlu_training_sentence OWNER TO chatbot;

--
-- Name: COLUMN nlu_training_sentence.nts_id; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.nlu_training_sentence.nts_id IS 'ID';


--
-- Name: COLUMN nlu_training_sentence.text; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.nlu_training_sentence.text IS 'Text';


--
-- Name: COLUMN nlu_training_sentence.smt_id; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.nlu_training_sentence.smt_id IS 'SmallTalk';


--
-- Name: person; Type: TABLE; Schema: public; Owner: chatbot
--

CREATE TABLE public.person (
    per_id numeric NOT NULL,
    login character varying(100),
    name character varying(100),
    grp_id numeric,
    password character varying(100)
);


ALTER TABLE public.person OWNER TO chatbot;

--
-- Name: COLUMN person.per_id; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.person.per_id IS 'Id';


--
-- Name: COLUMN person.login; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.person.login IS 'Login';


--
-- Name: COLUMN person.name; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.person.name IS 'Name';


--
-- Name: COLUMN person.grp_id; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.person.grp_id IS 'Group';


--
-- Name: seq_chatbot; Type: SEQUENCE; Schema: public; Owner: chatbot
--

CREATE SEQUENCE public.seq_chatbot
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 20;


ALTER TABLE public.seq_chatbot OWNER TO chatbot;

--
-- Name: seq_chatbot_node; Type: SEQUENCE; Schema: public; Owner: chatbot
--

CREATE SEQUENCE public.seq_chatbot_node
    START WITH 1025
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 20;


ALTER TABLE public.seq_chatbot_node OWNER TO chatbot;

--
-- Name: seq_groups; Type: SEQUENCE; Schema: public; Owner: chatbot
--

CREATE SEQUENCE public.seq_groups
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 20;


ALTER TABLE public.seq_groups OWNER TO chatbot;

--
-- Name: seq_media_file_info; Type: SEQUENCE; Schema: public; Owner: chatbot
--

CREATE SEQUENCE public.seq_media_file_info
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 20;


ALTER TABLE public.seq_media_file_info OWNER TO chatbot;

--
-- Name: seq_nlu_training_sentence; Type: SEQUENCE; Schema: public; Owner: chatbot
--

CREATE SEQUENCE public.seq_nlu_training_sentence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 20;


ALTER TABLE public.seq_nlu_training_sentence OWNER TO chatbot;

--
-- Name: seq_person; Type: SEQUENCE; Schema: public; Owner: chatbot
--

CREATE SEQUENCE public.seq_person
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 20;


ALTER TABLE public.seq_person OWNER TO chatbot;

--
-- Name: seq_small_talk; Type: SEQUENCE; Schema: public; Owner: chatbot
--

CREATE SEQUENCE public.seq_small_talk
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 20;


ALTER TABLE public.seq_small_talk OWNER TO chatbot;

--
-- Name: seq_training; Type: SEQUENCE; Schema: public; Owner: chatbot
--

CREATE SEQUENCE public.seq_training
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 20;


ALTER TABLE public.seq_training OWNER TO chatbot;

--
-- Name: seq_utter_text; Type: SEQUENCE; Schema: public; Owner: chatbot
--

CREATE SEQUENCE public.seq_utter_text
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 20;


ALTER TABLE public.seq_utter_text OWNER TO chatbot;

--
-- Name: small_talk; Type: TABLE; Schema: public; Owner: chatbot
--

CREATE TABLE public.small_talk (
    smt_id numeric NOT NULL,
    title character varying(100) NOT NULL,
    description character varying(100),
    is_enabled boolean NOT NULL,
    bot_id numeric NOT NULL
);


ALTER TABLE public.small_talk OWNER TO chatbot;

--
-- Name: COLUMN small_talk.smt_id; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.small_talk.smt_id IS 'ID';


--
-- Name: COLUMN small_talk.title; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.small_talk.title IS 'Title';


--
-- Name: COLUMN small_talk.description; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.small_talk.description IS 'Description';


--
-- Name: COLUMN small_talk.is_enabled; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.small_talk.is_enabled IS 'Enabled';


--
-- Name: COLUMN small_talk.bot_id; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.small_talk.bot_id IS 'Chatbot';


--
-- Name: training; Type: TABLE; Schema: public; Owner: chatbot
--

CREATE TABLE public.training (
    tra_id numeric NOT NULL,
    start_time timestamp without time zone NOT NULL,
    end_time timestamp without time zone,
    version_number numeric NOT NULL,
    status character varying(100) NOT NULL,
    log text,
    bot_id numeric NOT NULL,
    fil_id_model numeric,
    infos text,
    warnings text
);


ALTER TABLE public.training OWNER TO chatbot;

--
-- Name: COLUMN training.tra_id; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.training.tra_id IS 'ID';


--
-- Name: COLUMN training.start_time; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.training.start_time IS 'Start time';


--
-- Name: COLUMN training.end_time; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.training.end_time IS 'End time';


--
-- Name: COLUMN training.version_number; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.training.version_number IS 'Version';


--
-- Name: COLUMN training.status; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.training.status IS 'Status';


--
-- Name: COLUMN training.log; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.training.log IS 'Log';


--
-- Name: COLUMN training.bot_id; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.training.bot_id IS 'Chatbot';


--
-- Name: COLUMN training.fil_id_model; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.training.fil_id_model IS 'Model';


--
-- Name: utter_text; Type: TABLE; Schema: public; Owner: chatbot
--

CREATE TABLE public.utter_text (
    utt_id numeric NOT NULL,
    text text NOT NULL,
    smt_id numeric
);


ALTER TABLE public.utter_text OWNER TO chatbot;

--
-- Name: COLUMN utter_text.utt_id; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.utter_text.utt_id IS 'ID';


--
-- Name: COLUMN utter_text.text; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.utter_text.text IS 'Text';


--
-- Name: COLUMN utter_text.smt_id; Type: COMMENT; Schema: public; Owner: chatbot
--

COMMENT ON COLUMN public.utter_text.smt_id IS 'SmallTalk';


--
-- Data for Name: chatbot; Type: TABLE DATA; Schema: public; Owner: chatbot
--

INSERT INTO public.chatbot VALUES (1019, 'Alan', '2019-03-18', 'OK', 1202, 1041, 1040, 'Gestion des bases sur Mars');
INSERT INTO public.chatbot VALUES (1100, 'Oalia marché public', '2019-12-18', 'OK', 1580, 1321, 1320, 'POC av vente Oalia marché public');


--
-- Data for Name: chatbot_node; Type: TABLE DATA; Schema: public; Owner: chatbot
--



--
-- Data for Name: groups; Type: TABLE DATA; Schema: public; Owner: chatbot
--



--
-- Data for Name: media_file_info; Type: TABLE DATA; Schema: public; Owner: chatbot
--

INSERT INTO public.media_file_info VALUES (1202, 'alan.jpg', 'image/jpeg', 14139, '2019-10-07 13:57:13.151', NULL, '\xffd8ffe000104a46494600010101012c012c0000ffdb0043000302020302020303030304030304050805050404050a070706080c0a0c0c0b0a0b0b0d0e12100d0e110e0b0b1016101113141515150c0f171816141812141514ffdb00430103040405040509050509140d0b0d1414141414141414141414141414141414141414141414141414141414141414141414141414141414141414141414141414ffc0001108012c012c03012200021101031101ffc4001f0000010501010101010100000000000000000102030405060708090a0bffc400b5100002010303020403050504040000017d01020300041105122131410613516107227114328191a1082342b1c11552d1f02433627282090a161718191a25262728292a3435363738393a434445464748494a535455565758595a636465666768696a737475767778797a838485868788898a92939495969798999aa2a3a4a5a6a7a8a9aab2b3b4b5b6b7b8b9bac2c3c4c5c6c7c8c9cad2d3d4d5d6d7d8d9dae1e2e3e4e5e6e7e8e9eaf1f2f3f4f5f6f7f8f9faffc4001f0100030101010101010101010000000000000102030405060708090a0bffc400b51100020102040403040705040400010277000102031104052131061241510761711322328108144291a1b1c109233352f0156272d10a162434e125f11718191a262728292a35363738393a434445464748494a535455565758595a636465666768696a737475767778797a82838485868788898a92939495969798999aa2a3a4a5a6a7a8a9aab2b3b4b5b6b7b8b9bac2c3c4c5c6c7c8c9cad2d3d4d5d6d7d8d9dae2e3e4e5e6e7e8e9eaf2f3f4f5f6f7f8f9faffda000c03010002110311003f00fd53a28a2800a28a2800a28a2800a28a2800a28a2800a29ace141248000c926bc4be277ed7df0f7e1c34d6a9a89f116ad1e41b2d2312856f47973b17e9927dab1ab5a9d18f3549591e8e072ec5e6553d8e0e939cbc95edebd12f367b7138aa7aa6b363a25a35d6a37b6f616cbf7a7ba956241f5662057e7cfc40fdba7c7de296960d063b4f09d8b64036ebf68b9c7bc8e3683feea8fad781f883c4bac78b6f5aef5cd56f758ba273e6dfced31fc37138fc2bc2ad9d528e94a2e5f823f57cb7c31c7d74a78faaa92ecbde97e8bee6cfd26f157ed83f0afc2c5d0f8917569d0e0c3a4c2f73ff008f81b3ff001eaf2ad7ff00e0a25a2c0ceba2f83f51bd1fc325fdd476e3f250e6be1ca5af22a671899fc365f2ff003b9fa1e13c38c8f0ebf7ca551f9cacbff25e5fccfa9754ff00828478d2e370b0f0de8564a7a198cd3b0ffc7947e95cc5dfedc5f14ee5898eef48b41e9169a0ff00e84c6bc068ae3963f152dea33e969709645455a18487cd5ff3b9ee27f6d3f8b84ffc8c1683d869907ff1352c1fb6cfc5889b2fabe9d3fb49a647fd315e134547d7313ff3f1fdecea7c3792b56fa9d3ff00c023fe47d29a67edf7f112cf02eb4df0fdfaf7dd6b2c47f3593fa5769a1ffc145275c2eb3e0847f5934fd4307fef974ffd9abe37a2b68e658b86d3fc99e6d7e0bc8311f16152f4728fe4d1fa2be19fdbb7e1a6b6c89a83ea9e1f90f537d665e307fde88bfea057b1f84be27784fc7912bf87fc45a6eaf919d96b72ad20faa6770fc457e4353a3668a65963631caa72b221daca7d88e4577d3ceab47f8914ff0003e431be1865b593784ab2a6fced25f768ff0013f67b34b5f96fe04fda9fe25fc3f31476be229755b24c0fb1eb03ed51e3d0313bd7f06afa67e1b7edf9e1cd68c56be31d2a6f0e5c9c037b6a4dcda93ea401bd07e0df5af6a866d86aba49f2bf3ff33f32cd3c3ece72f4e74a2ab457f2efff0080bd7eeb9f57d1595e1cf14e91e2fd2e2d4b44d4ed755b093eedc59cab221f6c83c1f63cd6ad7b29a6ae8fcda709539384d59add3dc28a28a64051451400514514005145140051451400514514005145140051451400514563f8abc5ba3f82743b9d635dd420d334db71992e2e1b0a3d00ee49ec0649ec2936a2aecb8539d592853576f4496adb35c9c578a7c68fdabfc1df085a6b0129d7fc449c7f65d838fdd37fd3693a47f4e5bfd9af993e3bfeda9adf8edae347f0619fc3da01ca3dee76dedd0fa8ff0054a7d07cc7b91d2be65c7249e493924f73eb5f318bce146f0c3ebe7fe47ee9c3be1bcaaa8e27397cabf916ff00f6f3e9e8b5f34f43d53e2bfed2de39f8baf2c1a86a474dd19cf1a4e9ac62848f4739dd27fc08e3d857958500003803b0a5a2be5aa549d5973547767ef383c161b014950c2d3508ae895bfe1df9bd428a28acced0a28a2800a28a2800a28a2800a28a2800a28a2800a28a280373c1de3af10fc3dd55752f0deaf75a3de02373db3e1641e8e87e571ecc0d7d83f077f6f5b3be3069bf10ad174e9b851ad58a13031f5963e593eab91ec2be22a4aedc3e32b615feede9dba1f339cf0de5b9ec2d8ba7ef7492d24be7d7d1dd791fb2ba4eaf65aee9d6f7fa75dc17d6570bbe2b9b690491c8beaac383572bf27be11fc73f167c17d4fcfd06f77e9f23eeb8d2ae496b69fd4edfe06ff69707d73d2bf42be08fed1de17f8db61b2c25feced7624dd71a3dcb8f35077643d244ff006874ee057d8e0f32a58af75e92edfe47f3671270563b21bd787ef28ff325aaff0012e9ebb7a3d0f57a29334b5eb9f9d8514514005145140051451400514514005145140051457987c78f8efa37c0ef0bfdb6f3179abdc864d3f4c57c3cee3b9feec6bc6e6fc064902b3a95234a2e737648ecc2612be3ebc30d868b94e4ec92febef7b24687c62f8d5e1df82be1b6d4f5b9cbdc4b95b3d3a120cf74e3b28ec071963c0fae01fcdbf8bff1afc4bf1ab5efb7eb971e5d9c2c7ec7a640c7ecf6aa7d07f137ab9e4fb0e2b0bc7be3ed73e26789eeb5ff00105e35e5fcfc0ed1c283eec71aff000a0ec3f139249ae7ebe171d984f16f963a43b77f53fab385783b0d905355aada7887bcba47ca3fabddf92d04e94b4515e41fa28956ac74abfd4d1decac2eef513ef35b5bbca17ea541c57b5fec93f026d3e3378d2eee75b4693c39a32a49730292bf6995c9f2e22472170accd8e7000ef5fa3fa4e8f63a169f0d8e9d690585942bb63b7b58c471a0f40a30057b782cae58a87b494acba1f9771371dd0c8713f52a54bda545672d6c95f54b67776d7e6b53f1b18147646055d4e1948c107d08ed457e9bfed17fb39e89f187c317b736d6505a78ba089a4b2d46340af2b819114a47df46e9cf2a4e47a1fcc82ac8ccaea51d4e195ba823a835c98cc1cf0735193ba7b33e8786b8970fc4987955a51e49c34945eb6becd3ea9ebd16cf40a28a2bcf3ec028a28a0028a28a0028a28a0028a28a004ce2addbe937f796ad736fa7dddc5b2f59a1b7778c7fc08023f5afa63f62ff00d9eb4bf88d3def8bfc4d6ab7fa3e9f3fd9acec25198ae2700333c83f895015017a124e7a60fdf16d690d9dbc704112410c602a471a85551e800e00af7b0994cb134fda4a5ca9edd4fc9b88bc40a192e2de0a851f6b28fc4efca93ecb4777df6b799f8c8181e873f4a5afd02fdaeff670d1bc4fe0ed53c61a169f158f8974c89aeae0db2045be8546640ea382e172c1baf1839cf1f9f80e471c8af3b17849e12a724b5eccfb2e1ee20c3f11613eb34138b4ed28bdd3fd53e8c5ab3a5ea779a1ea56da869d77358df5b38960b9b7729244c3a1561d2ab515c49db547d34a2a49c64ae99f7e7ecd1fb60db78fded7c31e33961b0f12b623b6bfc0482fcf653d9253e9d1bb60fcb5f51839afc5fafb6bf650fdad1b5392cfc15e38bddd7a710e9bac4edcce7a2c3331fe3ecae7ef743ce09fadcbb34e76a8d77af47fe7fe67f3cf18f02ac3a966394c7ddde505d3ce3e5dd74e9a68bec7a2901cd2d7d41f848514514005145140051451400514566f88bc4161e16d12fb57d52e92cb4eb285a79e790e15114649ff00eb773c526d2576542329c9420aedec8e5fe31fc5cd1be0cf832e75ed59bcc7ff005569648d892ea623e58d7d3d49ec0135f977f107e206b5f143c597be21d7ae7ed17d72701572238231f7628c7655fd792724935d27c79f8cfa8fc6ef1c4dabdc6fb7d2adf30e9b62c78821cf523fbed8058fd074515e735f0798e39e2a7cb1f816de7e67f5a706f0ac321c37b7aeaf889ad5ff002afe55fabeafc920a28a2bc73f470a28a2803ed0ff008277788acd20f19682eea97ed2c17f1a1fbcf16d31b11fee90bff7d8afb3ebf1e3c1fe30d63c03e24b2d7b41bd7b0d4ed1b7472a8c820f05594f0ca47041eb5f5ff83ffe0a1969245690789bc2b2c17059526bbd3ae55a100900bec7c328039c65ba57d665b98d1a74951aaecd1fcf7c6bc1998e2f309e638087b48ced75749a6925d6d74ed7d35bf43ebdd5f54b5d174bbbd42f665b7b3b489a79e673854451b998fd0035f8ebabdf2ea9ac6a17c89e5a5d5ccb70a98fba1dd980fc8d7e917c7eb3bef8abf0b35cd134d95adde7804d6e2190e2e1908754623aabe318e9c8afcd2e412082ac3a823041f435867751ca708db4d4f57c30c242950c4d672fde3714d7649369fcdb7f70b451457cd1fb7851451400514514005145140051451401fa0dfb02f88acf50f83d77a4c6ea2fb4cd4a6f3e21f7b6cb8747fa1f987fc00d7d355f02fec37e1cd434fd5b5af182cd2c368b1ff67450ee223b87243b961dc200a07a1635eeff00173f6c1d13e126ad6da4dc68977aaea52dbfda1e3b59d15220490a18b7237609e01e3eb5f7782c4c69e0e12ada25a7f91fca1c4f91d7c6711d7a196af69293e66b4d1b5792bb76d1ff0096e7a7fc66f1259784be15f8ab54bf754b7874e9c61bf8dd90a227d599947e35f92112ec8d14f55007e95ebdf1d7f695f127c73961b5ba8e3d2340b793cc874bb672e19fb3cae71bd803c7000ec33cd791d7cde658b8e2ea2e4d91fb570470ed7e1fc14d629fef2a34da5aa496cafd5eaef6d05a28a2bc83f450a42334b45007de1fb1f7ed38de31b783c0fe2bbbddafc098d3afe66e6fa351feadcf79540ebfc4a33d41cfd5e0e6bf196d2ee7d3af20bbb49e4b6bab791658a789b6bc6ea72aca7b1046735fa65fb307c7b87e35f8336df3c7178a34c0b16a302f024cfdd9d47f75f0723b3023a633f659563fdaaf6155fbcb6f3ff827f3571ef09acbe6f35c0c6d4a4fde4becb7d57f75fe0fc9a4bda68a28afa33f170a28a2800a28a280109c0af83ff6e3f8eade21d6ff00e15f68d719d374e9049aac919e26b91cac3eeb1f53fed91fdcafa77f690f8bf1fc1bf8657faac2e8759b9ff43d3226e774ec0e188ee1002e7fdd03bd7e5acd349733493cf23cd3cae6492590e59d89c9627b924924fbd7cce718be48fd5e1bbdfd3b7ccfdbfc38e1e589acf37c447dd83b43ce5d65ff006ef4f3f3432968a2be40fe8e0a28a2800a28ab1a66997badea10d869d673dfdf4c711db5ac4d2c8e7d95412684afa214a4a29ca4ec915e90f239e7dabe84f037ec3bf11bc5714771aa259785ad58038d424f327c7fd728f383eccc2bd9fc3fff0004f1f0ddb053ad78af56d41bf896ca18ed97f50e7f5af4e965b8aaaaea165e7a1f118de35c87032709e2149ae914e5f8ad3f119fb2afc43ff84cfe1b45a75c4bbf54d08ad9c849f99a1c6617fc815faa57ce9fb4ff00c3c1e05f89971756d1797a5eb40df40147ca9213fbe41f463bbe8e2bee5f86bfb2e782be156a73dfe883533713c3e44a6eaf99d5d7208ca8006411c1c71cd6f78ffe04f83be26e9f6b67e20d364bb8ada5334252e648d918ae0e19483823a8f61e95f45530156be1634aa5b9d6dfd7a1f8de0f8b32fcaf3da98dc2297d5ea5eeac93d75d15eda4b6f26d1f941457e93ffc311fc27ffa025e7fe0cee3ff008aa3fe188fe13ffd012f3ff06771ff00c55791fd8b89eebef7fe47e85ff113724fe4a9ff0080c7ff00933f3628afd27ff8623f84ff00f404bcff00c19dc7ff001547fc311fc27ffa025e7fe0cee3ff008aa3fb1713dd7deffc83fe226e49fc953ff018ff00f267e6c515fa4fff000c47f09ffe80979ff833b8ff00e2a8ff008623f84fff00404bcffc19dc7ff1547f62e27bafbdff00907fc44dc93f92a7fe031ffe4cfcd8a2bf49ff00e188fe13ff00d012f3ff0006771ffc551ff0c47f09ff00e80979ff00833b8ffe2a8fec5c4f75f7bff20ff889b927f254ff00c063ff00c99f9b15358585c6ad7f6b6367119eeee655861897abbb10147e6457e90ffc311fc27ffa025e7fe0cee3ff008aad5f0bfec95f0d7c1dafd9eb3a668d7116a168c5e1924be96408c411bb6b31191938f4aa8e4b88bae66adeaffc8caaf89d942a7274a9d472b3b5d46d7e97f7b630fc25a0699f087e1b5a58492ac563a3d9b4b7771d37b005e593ea5b763f015f9f7e39f17dd78f7c61ab7882f32b35f4e641193fead3a220ff0075401f857ea4f8d7e0fe8be3bf0e5de87a8cb7a96174144ab6f36c66018301b80ce3206477af0bd77f600f09cc8c74cd5756b56ec3cf493f475feb5e8e3f055eb4630a4972c7ccf8de12e27cab2eab5b15984a5edaa3ded756ddeb7bddbdf4e88f83a8afa57c5dfb0ef883482e747d72d6f9872b6fa842d6ce7e8c3729fd2bc33c65f0efc4bf0fae043e20d1ae74d0c709348bba193fdd9172a7f3cd7cc55c2d6a1fc48b47ee797e7d966696584aea4fb6cfee767f81ced1494b5ca7bc1451450015d6fc28f897a9fc23f1d69de25d30976b76d9716dbb0b7303637c47ea3907b3053dab92a4aa8ca5092945d9a30af42962a94a8568f34649a69f54cfd89f08f8ab4ef1bf86b4dd77489c5ce9ba842b3c120ea54f623b107208ec4115b15f0afec1ff00195b49d6ee7e1f6a73ff00a1df96bad2cb9e239c0cc910f6751b80f556fef57dd20e457e8d83c4ac5515516fd7d4fe2fe23c96a64398cf072d63bc5f78bdbe7d1f9a62d14515da7cc85231c0a5af36fda1be257fc2aaf84baeeb91384d43cafb2d8827adc49f2a11feee4b7d14d67526a9c1ce5b23af0985a98dc453c3515794da4bd5bb1f0e7ed89f158fc48f8b17361693799a2f87f758db8539579b3fbf93f160101f48fdebc2e9325892cc5d8f2598e493dc9f7a5afcd2b55957a92a92dd9fdc196e02965783a582a3f0c125ebddfab7abf36145145627a41494b5eb9fb34fc0a9fe3878dfc8b9f321f0de9db66d4ae1382c0fdd854f667c1e7b2827ae2b5a54a55a6a9c16ace1c7636865b869e2f132b420aeff00aeef64bb96bf67efd98f5df8df75f6e95df46f0ac2fb65d49932f311d52053c31f563f2afb9e2bf41be1a7c20f0a7c25d2859786f498accb0026bb61bee273eb248796fa7007602ba7d1f47b2d034bb5d3b4eb58acac6d6358a1b78142a468060281d855dafbdc1e02961237de5dff00c8fe49e23e2dc771055716dc28f482dbd65ddfe0ba080014b4515e99f0c1451450014514500145145001451450014514500145145001494b450043736915dc4639a35910f6615c4f8a7c170c9653c4f6f1dfe9b2a9135b5c209171eea78615de521008a994549599d146bce8c94a2cf82fe337ec930b433eb1e048cc72a82f2e86cd9571dfc863d0ff00b04e0f623a57cab246f0c8f1ca8d1c88c5591c156520e0820f420f6afd67f12e8c34f9fcf89716f29fba3a2b7a7d0d7ca7fb557c0e8f58d3ae7c6da1db85d4ed577ea76f10ff008f98875980fefa8ebfde5e7a8e7e5330cb924ead156b6ebfc8fe81e10e339d49c303984b994b48cdee9f693eb7e8f74f7bad57c8745203914b5f2e7ee8145145005ad2755bcd0755b2d4f4f9dadafece64b8b7997aa4884329fcc57eb37c25f88569f14be1ee8be26b40a82fa00d2c20ff00a9987cb247ff000170c3e98afc8fafafbfe09fbf128d9eafadf81aee6fdd5d2ff6958ab1e922e16651f55d8dff00016af7728c47b2afecded2fcfa1f94788b932c7e59f5da6bdfa3afac5fc5f768fc927dcfb8a8a4a5afb83f9642be16ff00828378fcdf7897c3de0eb79330d8c2751ba553d6593291023d9039ff00818afb9dba57e4b7c6bf191f881f16bc55af6f3243737d225b93ff003c63fddc7ff8ea03f8d7839cd6f6741535f69fe0bfa47eb5e1b65cb179b4b1525a518dff00ede968bf0e67f238aa28a2be20fea30a28a28000aceca88a6476215517ab13c003dc9e2bf557f67af8550fc21f85fa4e8a6351a93a7dab51900e5ee5c02fcfa2f083d9457c03fb2c782d7c73f1dbc336b3279969652b6a5382320ac237283ec64f2c57ea428c0afabc9282b4abbf45fa9fcfde2866b2e6a395c1e96e797e2a2be566fe685a28a2bea8fc0c293354358d5a3d26dbcc61b9db84407ef1ff000ae32f75abdbf63e64cca9d9233b5454392474d2a12abaec8efde78e3fbceabf56029bf6c83fe7b47ff7d8af34233d79a4da3d3f4a8f69e475fd497f31e99f6c83fe7b47ff007d8a3ed907fcf68ffefb15e67b47a7e946d1e9fa51ed187d4d7f31e99f6c83fe7b47ff007d8a3ed907fcf68ffefb15e67b47a7e946d1e9fa51ed187d4d7f31e99f6c83fe7b47ff007d8a3ed907fcf68ffefb15e67b47a7e946d1e9fa51ed187d4d7f31e99f6c83fe7b47ff007d8a3ed907fcf68ffefb15e67b47a7e946d1e9fa51ed187d4d7f31e99f6c83fe7b47ff007d8a3ed907fcf68ffefb15e67b47a7e946d1e9fa51ed187d4d7f31e982ee16e92a1fa30a9430233dabcbb68f4fd2a682ea7b56dd0ccf19ff006588a7ed04f07da47a5d2d735a0f899aea55b6bac798784900c6e3e87deba40722b44efb1c13a72a6f96457d42cd2fed2581feeb8c67d0f635e6f34251a48a5404a928e8c320f620fb57a81e4570de2ab6fb3eacce0616650ff8f43fcab39aea7660e769381f9b3f1dfe1d8f867f12750d36dd0a697718bcb1f410b93f27fc05832fe02bcfebec1fdb57c28b7be0ed1bc431a7efb4ebbfb34ac07fcb29471f93a8ff00beabe3eafcf71d4550af28adb75f33fb0f8633296699552af51de6bdd97aad2ff3567f30a28a2b84faa0ae93e1af8d66f873f10340f12c24ff00c4b6ed269154e37c5f7655fc50b0ae6e91802083c8f4aa8c9c24a51dd1956a30c452951a8af19269af27a33f66eceea2bdb58ae20916582541246ea721948c823ea0d4d5e35fb2378cdbc69f01fc3924b2196eb4e46d32727ae613b57f34d87f1af65afd3a9545569c6a2eaae7f0be6183965f8bab849ef0938fdced7f99c57c69f157fc213f0a3c59adab1496cf4d99a261ff003d0a954ffc7996bf24914a2004e4818cfad7e8d7edd7aeb691f022e6d55b69d4f50b6b4233d543194ffe8aafce61d2be433ba9cd5e30ecbf33fa2fc30c22a595d5c4b5ace76f9452b7e2d8b451457cf1fb1051451401f5a7fc13c343fb478c7c61ac15cfd96c60b4524743248cc7f48857dd95f1effc13aadc2f877c6f3f77beb68ff05898ff00ecd5f6157dfe551e5c243ceff99fc87c7b55d5e21c45fecf2a5ff80c7f5b8521e052d21af58fcf8e13c4f746e75791739484041fccfeb59753ea0dbf50ba63de563fa9a82b95eacfa2a6b960920a28a2916145145001451450014514500145145001451450014514500264a90ca70c0e411d8d7a469d73f6cb2866ff009e8818fd7bd79bd775e166dda2dbfb6e1ff8f1ad61b9c18c5eea66bd72de358be4b597b8665cfebfd2ba9ae7fc66b9d3623e930fe46b496c70e1ddaac4f0dfda03471ae7c1af175beddcd1d89b94f66898483ff4135f9e03dabf4d7c716c2f3c17e208186449a6dd291f585abf322139890faa8fe55f199c46d5212f2febf33fa63c39aae584c452e8a49fdeadff00b68fa28a2be7cfd7428a28a00fb43fe09dde28cc3e31f0e48e708f06a30a67fbc0c727fe831fe75f6857e6ff00ec35aeb691f1eedad0361354d3ee6d48f52a1651ff00a2cd7e8f8e95f799454e7c2a5d9b5fafea7f26f88784586cfaa4d6d523197e1caff18b3e3fff00828a6a463f0ef82b4f0df2cd7b717057fdc8d547fe8c35f1057d79ff000514bb2de24f045b6784b4bb971f57887f4af90ebe633477c5cfe5f923f73e03a7ecf87b0de7ccff00f2790514515e51f7e145145007dcff00f04edff9137c63ff006138bff440afae6be46ff8276ffc89be30ff00b09c5ffa2057d735fa165bfee90feba9fc75c6dff25062bd57fe9310a434b486bd33e1cf36bcff008fdb8ffae8dfccd43535effc7edc7fd746fe66a1ae43e923b20a28aa9aaea96ba2e9b757f7d3a5ad95ac4d34d34870a88a3249a1bb6acb8c5c9a8c55db2dd15f2078c3f6d3d6ae353913c2fa4d9da69a8d849b5246966947f78a860a99f4e4fbd771f057f6aa4f1d6b96de1ff125941a66a774765adddab1104cfda365624a31edc904f1c1c579b0cc70f39fb352ff0023ecb11c219c61b0af175296895da4d3925ddaff002bb3e87a2901c8a5af48f8b0a28a2800a28ae0fe2f7c5cd2fe117875350be8daeeeee18c567631b0569dc0c924ff000a8c8cb7b80324d44e71a71739bb247561b0d5b195a387c3c79a72d125fd7fc31de515f1637eda3e3437fe72e97a22da67fe3d4c52138f4f337e73ef8fc2be90f837f1934bf8c1a1cb756b1358ea368552f2c1db71889fbacadfc48707071d883cd71d0c750c44b920f53e8b33e17ccf29a2b118982e4ead3bdafdff00cf6bf53d0e8a28aef3e4c43d2bb9f0a7fc81a2ff0079bff4235c31e95dcf853fe40d17fbcdff00a11ad21b9c58bfe1fccd8ac1f18ffc8293febb2ff235bd583e31ff0090527fd765fe46b596c79d43f8b13ce7c51ff22ceb1ff5e371ff00a29abf3020ff00531ffba3f957e9ff008a3fe458d67febc6e3ff0045357e6043fea63ff747f2af8fce7e2a7f3fd0fe91f0dff858af587e521f451457ce1fb2851451401e8dfb386a6da4fc7af02ce0eddda9c7013ed2068fff0067afd595fba2bf213e19dd9b1f895e11b907062d62cdb3ff006dd2bf5ec74afb0c8dfeee71f33f9c3c53a76c6e1aaf7835f73bfea7c21ff050ff00f91efc1fff0060c9ff00f472d7c9f5f5dffc1452d4af89bc11738e1ed2ea3cfd248cff00ecd5f22578599698ba9f2fc91fabf04352e1ec2b5da5ff00a54828a28af30fb80a28a2803ee7ff008276ff00c89be30ffb09c5ff00a2057d735f237fc13b7fe44df187fd84e2ff00d102beb9afd0b2dff7487f5d4fe3ae36ff00928315eabff4988521a5a435e99f0e79b5effc7edc7fd746fe66a1a9af7fe3f6e3feba37f3350d721f491d905792fed4eb747e086bff0065dd80f6e67dbff3cbce5ddf874cd7ad556d4b4eb6d5ec2e6caf604b9b3b98da19a190656446182a7d88acab43da53942fba68f472fc4ac16328e25aba84a32b77b34cfcb7156f46172dad69c2cb77db0dcc420d9f7bccde36e3df38afa3fc63fb14ea4ba9c92f85b5ab47d39d894b6d4cba4b08feeef5521c0f5201f5aee3e0bfecb16df0ff005a835ed7efe1d5f57b73bad60b7422deddff00bf96e5d876e001d7938c7c7c32dc43a8a2e365dcfe8bc5719e4f4f08eb53a9cd26b48d9ddbecf4d3cdeddae7bf9cee6ddf7b3ce3d7bd14806052d7da9fcc81451450021af8dbf6d8175ff09f787cc9bbec474b221f4dde73799f8fdcfd2becaae23e2c7c28d27e2df87469ba933db5c42c65b4be840325bb9183c1fbca47054f5c0e84035c38da12c450708ee7d4f0d6674b28cce9e2abaf7354fbabab5fe5f95cfce6af7afd8c56e8fc52d44c3bbeca34a93ed18e9feb13667fe059c7e3569bf628f160bff2d75dd18d9e7fe3e4f9a1b1ebe5edebedbbf1afa3be10fc1ed27e10e8325958bbde5f5cb07bcbf9542b4cc3a0007dd419385e7a924926bc0c1606bc6ba9cd5923f59e26e29cb2ae59530d86a8aa4ea2b2493d3cddd696e8b7bdb43bea28a2beb4fe7d10f4aee7c29ff2068bfde6ff00d08d70c7a5773e14ff0090345fef37fe846b486e7162ff0087f3362b07c63ff20a4ffaecbfc8d6f560f8c7fe4149ff005d97f91ad65b1e750fe2c4f39f147fc8b1acff00d78dc7fe8a6afcc087fd4c7fee8fe55fa7fe28ff00916359ff00af1b8ffd14d5f9810ffa98ff00dd1fcabe3f39f8a9fcff0043fa47c37fe162bd61f9487d14515f387eca145145006bf8333ff099f87b1d7fb4ed71ff007f92bf6217a7e26bf20fe1ada9bef891e12b71c99758b34c7fdb74afd7c5e95f5b91af76a3f43f9dfc5492f6f848f94bf389f1effc14574e32683e09d402f10de5cdb96ff7e3561ffa2cd7c455fa2bfb77684daafc0b92ed1373699a95b5d138e8ac4c47ff00460afce91d2bcbcde3cb8a6fba4ff4fd0fbcf0eabaad90421fc92947f1e6ff00db85a28a2bc63f4c0a28a2803ee7ff008276ff00c89be30ffb09c5ff00a2057d735f1eff00c13aae03787bc6f6fdd2fada4fc1a261ff00b2d7d855fa1659fee90f9fe6cfe3be3756e21c55fbc7ff004988521a5a435e99f0c79b5e7fc7edc7fd746fe66a1a9f504f2f51ba53da561fa9a82b94fa38fc2828a28a45094b45140051451400514514005145140098a5a28a0028a28a0043d2bb9f0a7fc81a2ff79bff004235c3d773e1652ba2dbe7bee3ff008f1ad21b9c58bfe1af535eb07c63ff0020a4ff00aecbfc8d6f573fe336c69b10f5987f235acb63cea1fc589e77e28ff916759ffaf1b8ff00d14d5f9810ff00a98ffdd1fcabf4e3c71702d3c17e209d8e163d36e589fa42d5f98f08c4283d147f2af8ece7e287cff43fa4bc374fd8e29f9c7f290fa28a2be74fd9028a28a00f44fd9d34d3ab7c78f025b81bb1aac5311ed18321ff00d02bf56d7ee8afcdcfd87b436d5fe3f58dd04dc9a6585cdd31c74254443ff461afd231d2bed3248da84a5ddfe88fe63f13ebaa99b52a4becc17dee52fd2c70df1cbc2c7c6bf08bc5da3282d2dce9b3794a3bc8abbd3ff1e515f92f1b6f40dd32338afda0700a9c8c8f4afc90f8c1e0e6f87ff14fc51a01429159dfc9e40231985cef8cff00df0cb5cb9e52f82afcbf55fa9eff008598e56c4e064fb4d7fe932ffdb4e428a28af953f7d0a28a2803eb2ff8278eb9f67f1a78bf472d8fb5d84376a09ea6290a9fd2515f7757e59fecbbe354f027c74f0c5ecd208eceee63a6dc127002cc36293ec1fcb3f857ea5a9c8afb8c9aa29e1b93f95ffc13f963c49c1bc3e75f58b695629fcd7baff04bef1690f22968af74fca4e13c516a6d75677c612601c7d7a1fd7f9d6557a0eada4c7aadb796ff002b0e51c75535c65ee857b62c7742d227f7e31b87ff005ab0946cee7b387ad194545bd51468a0fca704107d0f149b8566760b4526e146e1400b4526e146e1400b4526e146e1400b4526e146e1400b4526e146e1400b4526e153c1637376c04304921f65e3f3a60da5ab20da5d82a8cb138007735e91a75b7d8eca187fe79a053f5ef58ba0f864d9c8b717586987288390bee4f735d1018ada11b6acf231355546a31d90b5cb78da5f92d63cf5666c7e18feb5d41e0570de2ab9fb46acc80e561509f8f53fce9cf6230b1bd44fb1e4ff001ff581a27c1af175c6edacf62d6c9fef4ac231ff00a11afcf015f617eda9e2b5b1f06e8de1e8dff7fa95dfda64507fe594238fcdd97fef9af8fabe1f36a9cd5f95745ff04fea7e00c23a1953ad2ff97926d7a2b2fcd30a28a2bc53f4b0a28a43d2803eccff008277785c97f19788dd48ff0051a744d8eb8cc927f38ebed5af13fd8f7c187c1df01bc3fe6c663bad537ea9306183fbd394ff00c8623af6cafd172fa5ecb0d08bed7fbf53f8cb8bb1cb30cf313593ba52e55e915cbf8dae21af83ff00e0a07e023a678c741f17411e20d4edcd8dcb01ff002da2f9909f728c47fdb3afbc6bcbbf693f8687e29fc21d7348823f3352853edb6031cf9f1659547fbc3727fc0e8c7d0fac61e505beebe41c259aaca338a3889bb41be597a4b4bfc9d9fc8fcb0a2901e3a11ec7a8a5afce8fecd0a28a2800cb290cac5181c865ea0f623debf547f674f8ad17c5ef85da56aef22b6ab0afd93528c1e56e1000c71e8c30e3d9bdabf2babd5bf670f8e773f03fc702ee6125c787aff6c3a9dac7c9da0fcb2a0eee993c770587718f5b2dc5ac2d6f7be17a3ff33f3ee35e1f967d977ee15eb53d63e7de3f3e9e691fa954550d0b5cb0f1269169aa6977715f69f77189a0b981b724887a106afd7df269aba3f916519424e32566829314b453246b46aff79437d4537ecf17fcf34ffbe45494503b91fd9e2ff9e6bff7c8a3ecf17fcf35ff00be454945017647f678bfe79aff00df228fb3c5ff003cd7fef915251405d91fd9e2ff009e6bff007c8a3ecf17fcf35ffbe454945017647f678bfe79affdf228fb3c5ff3cd7fef915251405d91fd9e2ff9e6bff7c8a3ecf17fcf35ff00be454945017646208d7a228fa28a7e2968a04252d148cd814015efef12c2d259dfeea0ce3d4f615e6f3ce5da49a5700b12eeec70077249f4ad9f12eb435098430b66de339c8fe36f5fa57ca3fb54fc738b49b0b9f04e85701f52b95f2f53b888ff00c7bc47ac208fe361f7bfbabc753c79f8ac442841ce5d3f13ec720c9b119a626386a2b596eff963d5bfeb5765b9e0bf1dfe220f899f12750d4addcbe976f8b3b1f430a13f3ffc0d8b37e22bcfe900c52d7e7b526ea4dce5bb3fb0b0985a782c3c30d455a30492f97f5a8514515075056f7803c1f3fc41f1c685e1ab707ccd4eee3b76603ee213991bf040c7f0ac1afad7fe09ff00f0d4ea5e24d67c6f7517fa3e9e874fb2661d66700cac3fdd4dabff006d0d75e1287d62b469f7dfd3a9f3dc419a4727cb2b631bd62bddff0013d23f8efe47dc5a7d941a6d8dbda5b4622b682358a28d7a2a280140fa002acd20a5afd2b63f891b6ddd85211914b45023f323f6b7f8527e187c5bbd96d61f2f45d70b6a167b4615198fefa31feeb9ce3d1d6bc5abf50bf69ef83a3e317c32bbb2b5895b5db026f74c63c132a8e63cfa3ae57ebb4f6afcbd2ac8cc8eac8ea4ab238c15238208ec41e315f0199e17eaf5db5f0cb55faa3faef8233c59ce5718d47fbda568cbcff965f35f8a61451457927e841494b45007affc03fda57c41f03af4daaa9d5fc333c9be7d2a47c1463d64858fdc6f51f75bbe0f35fa0df0bfe35f847e2ee9a2e7c39aac73ceabba6b09bf777507b3c679fc4641ec6bf26aa6b2bdb9d32f22bbb2b89aceee13ba3b8b790c7221f5565208fc2bd7c1e65570ab91fbd1edfe47e73c47c1181cf64f1107ecab3fb4968ffc4b4bfaab3ef73f6641cd2d7e6c781ff6d7f897e108e382f6f2d7c4f68830175688f9b8ff00aea9863f560d5ecde1ff00f828969132a2eb9e0ebfb46fe27d3eea39d7f00e10d7d2d3cdb0b516af95f9aff23f11c6f87d9ee11bf674d545de325f93b3fc0fb068af9c2d7f6f5f86570019175cb53dc4ba76ec7fdf2c6ad7fc3757c2cff9fcd57ff0592575fd7b0bff003f17de7cfbe15cf13b3c1d4ffc059f42d15e13e1ff00db3be1b789b5dd3f48b1bbd4daf2fa74b7843e9d22a976381927a0f7af5b1e2ed3ff00bf27fdfb35bd3af4aaabd39267958bcab1d8092862a8ca0deaae9a36e8ac5ff84bb4ff00efc9ff007ecd1ff09769ff00df93fefd9ad7991c3ec6a7f2b36a8ac5ff0084bb4ffefc9ff7ecd1ff0009769ffdf93fefd9a39907b1a9fcacdaa2b17fe12ed3ff00bf27fdfb347fc25da7ff007e4ffbf668e641ec6a7f2b36a8af31f891fb44f83fe1547a7bebd3de46b7cceb0fd9ed1a5c9400b671d3ef0ae1ff00e1babe167fcfe6ac7fee1925734f17429cb967349fa9ece1f20cdb174956c3e1a728bd9a8b6bb7e67d0d495f39dc7ede3f0c6242636d6666f41a7b2ff335c9ebbff0504f0e2230d2f43d5a76ec5d238bf5663fcab29661858ffcbc47a34b8433eacecb0925eaadf9d8facaeaf21b388c9348b1a0eec6b87f1678dededec279a6b98b4ed3221996e6e2411ae3dd89c01eddebe2bf16fedbbe29d659c693a4596984f027bb91aee51f41f2a8fc8d78878bbc7be22f1edd0b8f106b175aa329ca24cffbb8ff00dd41855fc057975f39a5156a4aff0081f7795786d8fa9253c749535ff813fb969f8fc8fa23e32fed708f0cfa3f811d8b30292eb8ebb703bf90a79cff00b6c3e83bd7cb4eed2c8eeeed23bb16676249624e4924f5269b4b5f2f88c4d4c4cb9aa3ff00807eef94e4d83c968fb1c246d7ddbddfabfd365d10514515cc7b61451494013d8585ceab7f6b6365035cde5d4ab0410a0c99246215547d4902bf58fe0c7c38b7f851f0df45f0d43b5e5b58775ccca3fd6dc37cd2bfe2c4e3d80af903f613f8387c45e27b8f1e6a50674ed218c1a7071c4b7447cce3da3538ff0079ff00d9afbd40c0afb1c9b0bc9075e5bbdbd3fe09fcdbe25678b13898e5545fbb4f597f89ecbfedd5f8b6ba0b451457d21f8a051451400846457c03fb6e7c0b3e0ff137fc273a3dbe345d5e5c5fc718e2deecff001fb2c9d7fdf07fbc2beffac8f16785f4df1a787350d0f57b65bbd36fe1682785bba9ee0f620e083d8806b8719858e2e9383dfa7a9f55c379ed5e1fcc238a8eb07a49778bfd56ebcfcae7e3ad2d777f1abe116a9f05bc7375a15fee9ed5b33585f15c2dd404f0decc3a30ec7d88ae12bf3b9c254e4e125668fecac36268e328c311425cd092ba6baa0a28a2a0e90a28a2800a28a2800a28a6ababb6d560cde8a727f2a00ecfe0cffc95cf067fd85adfff004315fa3c3a57e757c18d32f5be2c7836416574635d56dc97103ed0378e49c62bf45c45263fd5bffdf26beaf275fba97afe87e03e22c93c6d0b3fb0ff00362514ef2dff00e79bff00df268f2dff00e79bff00df26be80fc96e86d14ef2dff00e79bff00df268f2dff00e79bff00df2680ba1b453bcb7ff9e6ff00f7c9a3cb7ff9e6ff00f7c9a02e8f96bf6e2ff8f1f06ffd76bbff00d063af94abeb4fdb734fbbb9b1f07986d2e260b35d6ef2a166c7cb1f5c0e2be4d9a37b6389a3784ffd3552bfcebe1f33ff007a97cbf247f4ff0004c93c8e82bff37fe9721b4522b061952187a839a5af30fb90a28a2800a28a2800a28a2800ae97e1afc3ed53e29f8d74cf0d690bfe9378ff003cc4656de21cbcadeca39f7381d4d73914525c4d1c30c6f34d230448e35dccec4e02803a92480057e93feca5fb3fafc1bf081bed52243e2bd5915ef1bafd9a3eab6ea7dbab11d5bd80af47038478babcbf656ffd799f17c57c454f87b02ea277ab2d20bcfbbf28eefe4ba9eb3e06f06699f0fbc27a5f87b4887c9d3f4f8443183f79bb9763dd9892c4fa935bd4515fa1c528a515b23f8eaa549d69caa5477949ddb7d5bdd8514514ccc28a28a0028a28a00f3af8e3f06349f8d9e0b9b46bfc5bdec44cd617e172f6b36383eea7a32f71ee011f97de34f066b1f0f3c4f7da06bb68d67a959bed743cabaff0ba1fe2461c83fd4115fb0d5e47fb42fecf9a4fc73f0e04729a7f88acd58e9fa9edced27931c98e5a363d4750791ce41f0f32cbfeb31f694fe35f89fa9f05f17bc8ea7d4f18ef8793ff00c01f75e4faaf9ad6e9fe5dd15b1e2ff07eb1e01f11de685af593e9fa9da36d9227e411d9d5ba329ea18706b1ebe1da71767b9fd4d4ea42b4154a6d38bd535aa69f54145148691a01603a9c01eb5efdf05bf639f167c53b6b7d5b5493fe116f0fca03c73dcc7bae6e14f431c471853d99c8f5008aebff00630fd9cadbc6730f1d789ad45c68f6b314d32ca65ca5ccca7e695c1fbc887803a16073c2f3f79818afa4cbf2b55a2ab57d9ecbb9f8971871dcf2ead2cbb2bb7b48e929bd795f64b66d756ee96d6bede21e0bfd8e3e18784228da5d0cf882ed704dceb3219f27feb9f083fef9af59d27c23a26828134cd1f4fd390745b5b58e203fef902b5e8afaaa742952568452f91f81e2f34c763e4e58aad29fab6ff0d909b68c52d15b9e5898a314b45002628c52d140098a314b4500263eb55eef4eb5bf8ca5cdbc5708782b3461c1fcc559a28dc69b8bba3ce3c55fb3b7c37f19230d4bc1da599187fafb5845b4bf5df1ed35f39fc52ff827fac704d7be01d6246914161a4eaee086f649c0183e81c1ff007857da7495c35b0387aebdf82f55a33ea72de28cdf2a92787af2697d993e68fdcf6f959f99f8e1e20f0f6a7e13d66eb49d66c27d3352b66db35b5c26d753dbea0f504641ec4d67d7e9f7ed1ffb3f69df1b7c28fe54715b78a2ca32da75f91824f5f2643de36ffc749c8ef9fcc6bdb2b8d32f6e2ceee07b6bbb691a19a09061a375243291ea0822be271d82960e76de2f667f4ff0bf1351e24c2b9a5cb561f147f26bc9fe1b3eee2a28a2bce3ed02919828249c01dcd0480324e07a9afb1bf64ffd92daedecbc6de38b22b02959b4cd1ae13973d567994f6eea87af56ec2bab0d86a98aa9c94ffe18f073aceb0991611e2b14fd17593ecbf57d3a9b3fb1c7ecc8fa30b4f1ff008aed0a6a0ebe6691a7ceb836ea47170e0f4720fca0fdd073d48c7d820605006296bf41c361e185a6a9c3fe1cfe3dceb39c4e7b8c963312f57a25d22ba25fd6af50a28a2ba8f0828a28a0028a28a0028a28a00292968a00f33f8dff00017c3df1c34016ba9a7d8f55b753f62d5615065b727b1fef213d50fd460f35f9b7f14be13788fe0f788df48f10d9f9458936d79164dbdda0fe28dbbfba9e4771debf5c2b9ef1c780b42f88de1eb8d17c43a745a969f37252418646ece8c39561d9860d78f8ecba18b5cf1d27f9fa9fa470b719e272092c3d6bce83e9d63e71ff002d9f93d4fc80abfe1ed06ebc55e20d3345b119bcd46ea3b487d99d8283f8673f857bafc77fd8efc45f0bdae356f0f09bc49e185cb968d337768bff004d107df51fdf51f503ad637ec69e1f4f10fed05a03c89e643a7c5717e7d32b19553ff7d48a7f0af8f585a91af1a15159b68fe8e967f83af9556ccf05514e308c9fa34ae935ba7e4cfd1df087862c7c17e18d2f42d3631158e9d6e96d0ae3f8546327dcf527d49ad8a45180296bf474945591fc5939caacdd49bbb6eedf9b0a28a2990145145001451450014514500145145001451450014514500211915f9e1fb767c3b8fc29f152d7c4169179769e22b732c9b4607da62c2c87fe04a636faeeafd10af98ff6fdf0f26a3f07ec3540999b4cd5626dde892ab46c3f329f957939a525570b2eeb5febe47e81c098f96073da293f76a5e0fe7b7fe4d63f3eea4b5b59efeea1b6b5824b9b999c471430a177918f015547249f415d77c2ff00841e29f8bfacff0067f86f4e69d5180b8bd97e4b6b61eb23e3affb232c7b0afd06f80dfb2ff86fe0a4097b81acf89dd36cbaace98f2f3d5615e7cb5f7fbc7b9ed5f2783c055c5bbad23dff00cbb9fd0bc45c5d80e1f83849f3d6e904f5f593fb2bf17d11e5dfb33fec6f1f86dacfc55e3db78ee3575225b3d15b0f1da1ea1e5ecf27a2fdd5f73d3eb80314018a5afb9c3e1e9e1a1c94d7fc13f95b38ce7199e625e27192bbe8ba45764bb7e2f7776145145749e185145140051451400514514005145140051451400514514008466b8ad1fe0e784fc3de3fb9f18e95a4c7a6eb7756cf6b70f6bf2453066562cc838df941f30c13939cd76d4544a11959c95ec7452c456a0a51a53715256767baecfba12968a2ace70a28a2800a28a2800a28a2800a28a2800a28a2800a28a2800a28a2800ae6fe20f80348f89be18b8f0febb1493e997124524b1c5218d9bcb915c0dc39009500e39c679ae928a99454938c95d335a556a50a91ab4a4e328bba6b74d6cd199e1cf0ce95e11d22df4bd174fb7d2f4eb71b62b6b58c222fe03bfa9ea6b4e8a29a492b22673954939cdddbddb0a28a2990145145001451450014514500145145001451450014514500145145001451450014514500145145001451450014514500145145001451450014514500145145001451450014514500145145001451450014514500145145007fffd9');
INSERT INTO public.media_file_info VALUES (1580, 'bot4.jpg', 'image/jpeg', 11140, '2019-12-18 16:25:18.13', NULL, '\xffd8ffe100c245786966000049492a000800000007001201030001000000010000001a01050001000000620000001b010500010000006a000000280103000100000002000000310102000e00000072000000320102001400000080000000698704000100000094000000000000006000000001000000600000000100000050686f746f46696c747265203700323031393a31303a30392031353a35323a353100030000900700040000003032313002a00300010000002c01000003a00300010000002c010000ffdb0043000302020302020303030304030304050805050404050a070706080c0a0c0c0b0a0b0b0d0e12100d0e110e0b0b1016101113141515150c0f171816141812141514ffdb00430103040405040509050509140d0b0d1414141414141414141414141414141414141414141414141414141414141414141414141414141414141414141414141414ffc0001108012c012c03012200021101031101ffc4001f0000010501010101010100000000000000000102030405060708090a0bffc400b5100002010303020403050504040000017d01020300041105122131410613516107227114328191a1082342b1c11552d1f02433627282090a161718191a25262728292a3435363738393a434445464748494a535455565758595a636465666768696a737475767778797a838485868788898a92939495969798999aa2a3a4a5a6a7a8a9aab2b3b4b5b6b7b8b9bac2c3c4c5c6c7c8c9cad2d3d4d5d6d7d8d9dae1e2e3e4e5e6e7e8e9eaf1f2f3f4f5f6f7f8f9faffc4001f0100030101010101010101010000000000000102030405060708090a0bffc400b51100020102040403040705040400010277000102031104052131061241510761711322328108144291a1b1c109233352f0156272d10a162434e125f11718191a262728292a35363738393a434445464748494a535455565758595a636465666768696a737475767778797a82838485868788898a92939495969798999aa2a3a4a5a6a7a8a9aab2b3b4b5b6b7b8b9bac2c3c4c5c6c7c8c9cad2d3d4d5d6d7d8d9dae2e3e4e5e6e7e8e9eaf2f3f4f5f6f7f8f9faffda000c03010002110311003f00fd53a28a2800a28a2800a28a2800a28a2800a28a2800a293354b54d6ec745b7335f5d456b1f6323633f41d4fe159d4ab4e8c1d4ab2518addb764be6546329b518abb2f5266bccb5cf8db690168f4ab37bb61ff002da73e5a7e03a9fd2b84d5be247887582c1efdad623ff2ced47963f31cfeb5f9be65e20e4b816e14a4eacbfbab4ffc09d97dd73e8f0dc3f8dafacd722f3dfeeff3b1efd7dab596989baeeee0b55eb99a40bfccd7357df15fc3565902fcdcb0ed6f133feb8c7eb5e052169642f23191cf5673927f13498afceb19e2863aa36b0942305fde6e4ff0e55f99f4547862847f8b51bf4b2ff33d8eebe38e9887fd1f4fbc9bddcaa7f5359f2fc757ff00965a301fefdcff0082d796515f31578ff886abd2ba8fa463faa6cf4e19065f1de17f56ff00ccf4b3f1cafb3c6936ff00f7f9bfc29c9f1ceec1f9f48848ff0066761ffb2d799515c8b8df8853bfd69ffe030ffe44d7fb132fff009f5f8bff0033d661f8eb11c79da348bef1ce0ff302b56d3e356853604d15e5b1ee5a20c3ff001d26bc468af468f8859fd27ef558cfd62bff006de539e7c3f8096d16bd1bfd6e7d1ba7f8fbc3fa990b06ad6fbcff000cade59fc9b15be922c881958329e8ca720d7ca4541ebcd5cd3f57bfd21f7d8decf687fe9948547e5d0d7d6e0bc52ac9a58dc327e706d7e0efff00a523c9adc2f07ad0a96f55faab7e47d454b5e1ba37c65d66c0aadf450ea31f72479727e638fd2bd0341f8a9a16b456379ce9f7078f2eebe504fb374fe55fa6657c6992e68d4215b926feccfdd7f7fc2fe4ee7cd62b26c6e17594399775affc1fc0eca8a6ab875041c83c823bd3abee4f0c28a28a0028a28a0028a28a0028a28a0028a28a0028a28a0028a28a0028a28a0028a2909a005aa3ab6b565a1da35cdf5ca5b423f89cf53e807527d8571de34f8ad69a134969a704bebf1c336731447dc8ea7d87e26bc7356d62fb5ebc3757f72f7331e858f0a3d147403e95f9571171f60f2972c3e092ab557fe0317e6fabf25f3699f53976435b176a95bdc87e2fd3b7abfb8eff00c4df19ee6e4bc1a2c3f668fa7da6600b9ff757a0fc735e75797971a8dc35c5dcf25ccedd64958b35420628afe78cd73dcc33aa9cf8daae4ba2da2bd16df3dfbb3f43c2e070f828da8c6de7d7ef0a28a2bc03bc28a28a0028a28a0028a28a0028a28a0028a28a0028201a28a00dbf0f78d358f0c328b3ba260079b69be78cfe1dbf0c57ac785be2ce99ae1482f7fe25978dc012366373ecddbe8715e19411915f6b9271766991b51a53e7a7fc92d57cbac7e5a774cf171b946171d7728da5dd6ff3ee7d5a082296be7ef08fc49d4bc2ec9048c6fb4f1c791237cc83fd86edf43c7d2bdafc3de27d3fc4f65f69b09c48a3878db878cfa30edfcabfa4787f8b32fe208f2d27c957ac1eff0027f697a6bdd23f38cc329c465eef2578f75faf635a8a28afb53c50a28a2800a28a2800a28a2800a28a2800a28a2800a28aaf7b7b069d692dcdccab0c11296791ce028a994a308b949d92dd8d272765b8fb8b98ad219269a458a28d4b33b9c0503b935e31e3bf8a736b064b1d21dedec7eebdc0f95e6fa7755fd4fb56578f3e205c78bae0dbc1ba0d290fc911e0ca7fbcff00d076fad724062bf9b78bb8eaa639cb0395cb9696ce4b472f25da3f8bf4dff47ca3228d04abe295e5d1745ebe7f90814014b4515f8c1f66145145001451450014514500145145001451450014514500145145001451450014514500156f4ad5af343be4bcb19dade74fe25e847a11dc7b1aa9456b4ea4e8cd54a726a4b54d68d3f264ca319a7192ba67be7817e22daf8b2316f305b5d4d465a1cfcb27a94f5fa751fad7620e6be548a592de54962768a542191d0e1948e841af6cf875f1217c42aba7ea2cb1ea6a3e57e8b381dc7a37a8fc47b7f48f07f1c2cc5c72fcc9daaed196ca5e4fb4bf07e4f47f9be71923c35f1186578755dbfe07e47a051499cd2d7eca7c7051451400514514005145140051452138a0064d324113c923048d01666638000ea4d782fc43f1e49e2cbc36d6acc9a542df20e86661fc67dbd07e3d6b6fe2cf8e4de4cfa1d8c9fb88ce2ea453f7d87f07d077f7e3b579a0afe70e3de2c78aa92ca7032fddc749b5f69ff2fa2ebddf92d7f46c872954a2b175d7bcf65d977f57f800e28a28afc4cfb50a28a2800a28a2800a28a2800a28a2800a545692454452eec42aaa8c927d00a4af45f82ba2c37babdeea12a876b3555881eccd9cb7d7031f8d7b792e593ce730a580a6ece6f7ec926dbfb9338b1b8a8e0f0f3af257b7fc322969bf0775fbfb712cad6d625864473392ff00885071587e24f04eade14c3dec01add8ed5b885b7464fa1ee0fd6be91c556d4b4e8355b19ed2e6312413214753dc1afdff0017e1a6592c2b8616728d54b493774df9ab6de963e028f12e29554eaa4e3d92fcbfe09f2dd152dddb1b2bcb8b663b9a195a327d7048cfe95157f344a2e127196e8fd2d35257414514540c28a28a0028a28a0028a28a002951de291248dda391086575382a47420d2514d369dd0b73ddbe1bf8f97c4f69f64bc609aac2b96ec265fef8f7f51f8d76f5f2c58decfa5de43776b2186e2160e8e3b1ff000f6afa23c17e2c83c5ba3a5d2011ce9f24f083f71ffc0f515fd41c0dc58f37a5f50c64bf7f05a3fe78affdb975eeb5ee7e639e655f5497b7a2bdc7f83ff27d3eeec741451457eb47c985145140051451400571bf12fc63ff0008be8fe5dbb81a85d0290e3aa0fe27fc3b7b9aeb2eeea2b2b696799c470c4a5ddcf40a0649af9b3c53e2197c53ae5c5fc995463b628cff000463ee8fea7dc9afcdb8e7885e4b80f6341dab55ba5e4bacbf45e6efd0fa4c8f2ffaed7e7a8bdc8eafcdf45fe6650c9e49249e493d4d14515fc9c7eae145145200a28a2800a28a2800a28a2800a28a2800aedbe1478a20f0f6b935bddb88ad6f542798c70a8e09db9f639233f4ae2682335eae5798d6ca7194f1b43e283bfaf46be69b472e2b0f0c5d1950a9b33eac5604039e2b27c4fe24b5f0c6952de5cb80402238f3f348fd947f9e2b8af8557d727c2a419e46093baa866ced002e00cf415c0fc43b896e7c5f7fe6caf208d82a0762428da381e95fd259bf19cb0b9252cc70f4bdfab64aef48b69bbf9dade573f37c1e4caae36587a92d21bf9d9afb8e7a599ee6696690e6491cbb1f72727f9d368a2bf969b72777b9fa8a56d10514515230a28a2800a28a2800a28a2800a28a2800addf0678a65f096b91dd0dcd6aff0025c463f893d7ea3a8ffebd6150466baf098bad81c443138795a70774ff00afc7ba31ab4a15e9ba5515d33ea9b6b98eee08e685c49148a1d1d4e43023208a96bcafe0d78accb149a15c3fcf10325b13dd7f897f0ea3d89f4af53afed1c8b37a59e6029e369697dd7692dd7f9775667e338ec24b0588951974dbcd7462d14515ef9c014514c96458a367760a8a09663d00ee693692bb03cd3e33f897ecb610e8d0b625bafde4d8ed183c0fc48ffc76bc7c569f89b5b7f11ebf79a836764aff00bb07f85070a3f2fe75995fc63c519c3cef34ab8a4fdc5eec7fc2b6fbf7f99fb365783582c2c6975ddfabff002d828a28af933d50a28a2800a28a2800a28a33400514668a0028a28a601453ede09aee7586de279e67e1638d4b31fa015d4d97c2bf12dea0636496ca7fe7e25553f90c9af470796637306d6128ca76fe58b76f5b6c7356c4d0c3ff00166a3eaec75ff0abfe45693febe64fe4b5c1f8f7fe46fd4ffdf5ff00d016bd63c11e0ebff0ee88d6974f0194ccd27eedc918207b0f4ae53c5df0bb5cd535dbcbeb5fb34914ac1954ca55bee81dc63b7ad7ec99d6499955e1cc261a9d0939c1c6e92bb5eec96df33e3f058dc34331ad5255128bbd9fcd1e67456beb3e11d674052f7da7cd1443acaa03a7fdf4b903f1ac80735f896230d5f093f65888384bb4934fee67da53ab0ab1e6a724d793b8514519ae7350a28cd19a4014519a2800a28a2800a28a2800a28a2802ce99a8cfa36a56d7d6c713412075f7c7507d88c8fc6be98d1f538759d32daf6dce619e3122fb67b7d474fc2be5e2335eb5f04fc4064b6bbd1e56cb427cf841fee93f30fc0e0ffc0abf60f0df39784c7cb2ea8fdcadb79497f9abaf3691f21c4783f6b875888ad61bfa3ff27fa9ea9451457f4d9f9985719f15f5b3a4784ae111b6cd7845b260f383cb1ffbe41fcebb235e2bf1af56fb56bd6960ad94b58b7b0ff69cff00801f9d7c4719e62f2dc92bd48bb4a6b917acb47f72bbf91ede4d87face36117b2d5fcbfe0d8f3c03028a28afe3b3f600a28a2800a28a2800a7451c93ca91448d2cae70a8832cc7d00a96c6c67d4ef61b4b58ccb713304441dcff00857bf782bc0767e12b556016e35071fbdba239ff00757d17f9f7afb5e1ae17c5711d66a0f92947e297e89757f975e97f1732cd2965d05cdac9ecbf57e479ae87f07759d4d564bd923d3223fc2ff3c9ff007c8e07e26bafb1f827a342a3ed3737774fdfe711afe407f5af430314b5fd0b80e04c8b0514a547da4bbcddff000d23f81f9ed7cf71d5de93e55d969f8eff0089c845f0a3c3110c1d38c9eef3b9feb52ffc2aff000c7fd0263ffbf8ff00fc55755457d14787f278ab2c1d3ffc023fe479ef1f8c7ff2fa5ff813ff003395ff008561e18ffa04c7ff007f1fff008aa3fe1587863fe8131ffdfc7ffe2abaaa2abfb0728ffa04a7ff008047fc85f5fc5ffcfe97fe04ff00ccc9d17c2da57877ccfeceb28ed4c9f7997258fb64e4e3dab5702968af5a861e8e1a9aa5420a315b24925f723967527565cd51b6fbbd429314b456e6635d03a9560083c107a1ae664f867e19965791b488b7312c76bba8cfb00702ba8a2b871580c2636df5aa319db6e68a95bd2e99bd2af5685fd94dc6fd9b5f91caff00c2aff0c7fd0263ff00bf8fff00c551ff000abfc31ff4098ffefe3fff00155d5515c1fd83947fd0253ffc023fe46ff5fc5ffcfe97fe04ff00cce49fe167865871a605ff007657ff001aa175f07f41941f2a29623fecccdfd735de515854e1bc9aaab4b094fe508afc9171ccb1907755a5f7b3c7f55f8369164dadf4b137617081d4fe231fcab88d73c25aa787b2d7506e8338f3e23b93f13dbf1afa55d03a904020f506b1afec44591b43c2fc10c323e87d6be0f38f0fb2cad073c2a74df75aaf9a7d3d2c7bd83e20c4c24a357de5fd75ff003b9f36039a2bb8f1f781934a56d4b4e4db699fdf403fe5967f887fb3eddbe9d3870735fcf599e5988ca7132c2e256ab67d1aeebcbfe18fd070d89a78ba6aad37a7e41451457927585145140056c783f5aff847fc4da7de9388964092ff00b8dc37f3cfe158f48c32315d585c454c257862293b4a0d35ea9dd1955a71ad095396cd5bef3ead53914eae7fc07ab1d6bc27a6dcb1cc9e508e4ff797e53fcb3f8d7415fdcd84c4c31987a789a7f0ce2a4bd1ab9f8755a6e8d495396e9b5f708dd2be69f186a3fdade2ad56eb3b95a76553fecafca3f415f466ad77f60d32eee7fe78c2f27e4a4d7cb6a4b0dc7ef1e4fd6bf12f14f16d53c2e11757293f9592fcd9f6dc2f4af2ab55f4b2fbf57f92168a28afe7c3f400a28a2800a28a4638526803d5fe0a78754a5d6b52ae58936f067b01f7cff21f81af58ac2f03e9c34af09e976e0608815dbfde6f98fea6b76bfb4b8632d8e559450c3a567ca9cbfc52d5ff0097a247e3199e25e2b1752a3daf65e8b60a28a4cd7d49e58b4534b85ea71f5a6fda231ff2d13fefa149c92dd8ecd9251517da22ff009e89ff007d51f688bfe7a27fdf54b9e3dc2cfb12d1517da22ff9e89ff7d51f688bfe7a27fdf5473c7b859f625a2a2fb445ff003d13fefaa3ed117fcf44ff00bea8e78f70b3ec4b4545f688bfe7a27fdf547da22ff9e89ff7d51cf1ee167d8968a8bed117fcf44ffbea8fb4c5ff003d13fefaa39e3dc2cfb12d15189e36e8ea7e8453f34d34f60b585a8a7884b1b21e878a9690f4a1a4d598b639bb8b759639609903a3028e87a107822bc175ed29b43d66eec4e4ac4ff213dd0f2a7f222be86d41365d93fde00d7927c5ab2116ab6376063ce88c6c7d4a9e3f46afc2bc40cba35303f594bdea52dffbb276fceccfbac8310e35fd9f492fc56bfe670b451457f3d1fa085145140051451401ec1f03f5132e93a85893cc1389147b38ff00153f9d7a75787fc16bcf23c53716e4f1716c703dd5811fa135ee15fd71c058b78ac868a6f583947ee775f8347e4b9f52f658f9dbed59fe1fe6737f11ae4daf8275871c13014ffbe885feb5f3a8e95ef7f1725f2fc0d7a3fbef12ff00e3e3fc2bc1074afc9fc4eaae59bd2a7d1535f8ca47d5f0cc6d849cbbc9fe4828a28afc7cfaf0a28a2800a02798ea9fde60bf9f1484e2bd83c0ff0009ad61b586fb5a8ccf74d875b524848bb8dd8eadfa0afa4c8b20c667f88f61844acb5937a24bcfd7a25afc933cdc763e8e5f4f9eabdf64b767a5c3188a2545e8a028fc05494806296bfb59249591f8b6e2138accbbd41998a447007058753572fa4315b391d7181f8d6301815e6632b4a16844eaa3052f7981cb1c9393efcd1814b4578c77098a314b45002628c52d140098a314b45002628c52d140098a302968a004da29f1cd240728e47b76a6d14d3717742693d19ad67762e5391871d455aac4b37f2ee93d1be535b43a57d0e16abab0bbdd1e7558724b433355189633ea0ff3af35f8bb106d2ac25ee97057f353fe15e99ab7de8bf1ac1d6344b3d76d7ecf7b0f9b16770c31054f4c823bf35f1bc49819e6585af84a6d29492b5f6be8fcfb1ece5b5d61ead3ab2d91e0345741e31f0949e16bb4d8ed3594d9f2a461c823aab7bff3ae7ebf93b1983af80af2c36223cb38eebfae8fa1fab51ad0af4d54a6ee98514515c46c1451450074df0d2e3ecde3ad2cf67678cfe28d5f43af4af9abc1b2795e30d15bfe9ee31f99c7f5afa5474afe95f0bea3965b5e9f6a97fbe31ff23f36e278db1309778feace1fe31ffc89737fd778bff42af0a1d2bdebe2ec7bfc0f787fb9244dff008f8ff1af051d2be0bc4b4d67717de9c7ff004a91ef70d3be09ff0089fe4828a28afc9cfab0a28a28037bc05a62eafe31d32ddc068c49e6b83d08405bf9815f4781c57847c1e8c3f8d549fe1b6908ff00c747f5af78afe9df0ca8469e5152b5b594dfdc92b7ebf79f997135472c5c61d147f36c28a28afd78f9128eaa716e3dd8566d696abff1ee3fdefe959b5e0637f8a7a143e00a28a82f6e92c6d26b894e23891a4623d00cd79f292827293b2474a4dbb226dd804f61de8cf00f635e0daef896fbc4576d2dc4aeb167f776eac4220ec31dcfbd3bc3fe28bdf0ddda4b0caef6f9fde5bb312ae3bf1d8fbd7e56bc41c23c4fb3f62fd9dedcd7d7d796db7ceff3d0fa8fec0adecf9b9d7376ff0083ff0000f78a2a2b6b84ba8239a33ba39143a9f5046454b5faa26a4935b1f2ed5b461451455082909c52d721f10fc533787ec228ad5b65ddc92164fee28ea47bf200af3f1f8ea396e1a78baefdd8af9f64979b7a1d14284f135634a1bb3aedc320773da8cd7ceb25c4f34a6592691e52725d9c96cfd6bd1be1b78b6e6f6e1b4bbd95a76085e095ce5b03aa93df8e47d0d7c2e51c6d87ccf171c254a4e1cda45def77d13d15afd37d4f6f1792d4c35275632e6b6fa58f45a28a2bf4a3e7014e2443e847f3adf15803efafd47f3adf15ec603697c8e2c46e8ced5bef45f8ff004aa357b56fbd17e3fd2a8d7162ff008d2feba1bd1f811cdfc41d3d6ffc297d91978544e87d0a9e7f4cd78a8e95effaec625d175053d0dbc83ff1d35f3fa728bf4afe7cf10a8c638da3596f28b4fe4ffe09f7fc3f36e8ce1d9fe6bfe00b451457e527d5051451401a7e16ff0091a747ff00afc8bff4315f4d0af9a7c1d1f9be2fd157fe9ee33f9367fa57d2c3a57f47785abfd8b12ffbebf23f39e287fbea6bc9fe6735f126dcdcf823575032443bff00ef960dfd2be771d2bea1d6acff00b4349bcb6c67ce85e3fcd48af975010b83d4706be77c52a0e38dc3623f9a0d7fe02eff00fb71e8f0bd4bd0a94fb3bfdebfe00b451457e267da851451401dafc1f9447e36453fc76d228fd0ff004af79af9afc15aaae89e2cd32ee46db12cbb2463d9581527f5cfe15f49a9c8afe9bf0cb131a99555a17f7a137f734adf8a7f71f99f135371c5c67d1c7f26ff00e00b451457ec07c814755ff8f71fef7f4acdad2d57fe3dc7fbe3f9566d7818dfe29e850f802a96b162752d2aeed01da6785e304f62471576908cd79b529c6ac254e7b3567f33aa32716a4b747ceb3dbcb677125bce8639a362ae8c390696dad66bfb98adadd0cb3cadb5100ea6bdd757f0be99ae307bdb349a4030241957c7a64734ba478674cd0893656890bb0c19396723d3279afc49787b88facd9d65ecafbebcd6f4b5afe77f3b743ed3fd60a7ecefc8f9ff000ff3fc0b5a65a7d82c2dadb3bbc98963cfae00156a9296bf6e84234e2a11d9687c536e4db61451455882bcf7e2ce9334f6d69a846a592df724b8fe10c410df4c8c7e22bd0a9ae8aea55806523041190457919b65d0cdb055307376e6ebd9a774fef475e1310f0b5a35a2af63e73cd767f0b74996eb5c6bfda45bdb232efec5d86303f0c9fcabb997e1f68134fe69d3901272551d954ffc041c56e5ad9c36502436f12430a0c2a46b802bf35c97822be0b1d0c562ea45c60ee946fab5b5ee95bbf5ec7d1e373b856a0e9528bbcb47725a5a28afd84f9101f7d7ea3f9d6f8ac01f7d7ea3f9d6f8af5f01b4be47162374676adf7a2fc7fa551abdab7de8bf1fe9546b8f17fc697f5d0de8fc08cfd7e411687a839e8b6d21ff00c74d78020c20fa57b3fc46d496c3c2d74b9c497388107ae4e4fe80d78c8e95fcf5e20d78cf1d468ade31bbf9bff807e819041aa139beaff20a28a2bf2b3ea428a28a00e97e1b5bfdabc73a4aff0071da43f8231afa217a0af0ef83167f68f164d391c5bdb31cfbb1007e99af72afea2f0d287b2c9a551fdb9b7f24a2bf34cfcc3896a7363147b457eac46e95f3378ab4ff00ec9f13ea9698dab1dc3ed1fec93b87e8457d327a5788fc68d28da7892def947c9770e09ff6d383fa15acbc4cc0baf95431515ad296be92d1fe3ca570d57f678a9527f697e2b5fcae701451457f311fa6851451400846457a6f823e2d358416fa76ad1493a2e238ee62e5b1d00607afd45799d3a27f2e689ffbaeadf91af7b26ce7199262557c1cf95bd1add35e6bfa6ba33831983a38da7eceb46fdbba3eab1c8a5a6a10cb91c83cd3abfb74fc4ca9a8a6eb66c76c35648e95beea1810791d2b16e6d9ad5f9e50f46af1f1d4ddd544765092f8591d1499a335e51d82d1499a33400b452668cd002d1499a33400b452668cd002d1499a33400b452668268024b64f32e635f7cd6e0e95434fb43166471866e00f415a15efe0e9ba74ef2dd9e7569294b433756fbd17e35cdf893c456fe1ab0175709248ace23548c0c96c13dfa74ae8f563fbc887b1fe75e6ff001724c689649ddae73f929ff1af8fe26c654c0613118aa2fde8ad2fdf447b196518d7ab4e94f66ce0fc4de26baf145eacd3011431e445029c841dce7b93eb5914515fc9789c4d6c656957af2e69cb56dff5ff000c7eaf4e9c28c153a6ac90514515cc6a145148c7033401ebdf0374fd9a7ea77c47fad956153eca327f56fd2bd46b9bf879a49d1bc23a6c0cbb6568fce93fde7f9bfa81f857495fda7c2f8179764d86c3c959f2a6fd65ef3fc59f8c6675feb18cab516d7b7c969fa0570ff17745fed4f0a493a2e66b27138c75dbd1bf439fc2bb8a8ae604b98248a450f1c8a5194f704608af5334c04333c0d6c14f69c5af47d1fc9d99cb85aef0d5e15a3f65dcf9581cd157f5fd1e4f0f6b779a7499cc1215527f897aa9fc411542bf87ab519e1eaca8d556945b4d766b467edd09c6a454e2ee9ea14514562585238ca914b41e9401f4d7866f86a7e1fd3ae81dde6dba31faed19fd6b52bcebe0c6bab7ba049a73b7efac9ced07bc6c723f23b87e55e8b5fdbb90e3e399e5787c545df9a2afeab492f934cfc4f1f41e1b135293e8ff000e9f805359430c1008f434ea2bde380aafa740c7ee6dff0074e299fd9517f79ff3abb4560e8527bc51a2a925d4a5fd9717f79ff3147f65c5fde7fcc55da297d5e97f287b49f7297f65c5fde7fcc51fd9717f79ff00315768a3eaf4bf943da4fb94bfb2e2fef3fe628fecb8bfbcff0098abb451f57a5fca1ed27dca5fd9717f79ff003147f65c5fde7fcc55da28fabd2fe50f693ee52fecb8bfbcff0098a3fb2a2fef3fe62aed147d5e97f287b49f7290d2e11ddcfd5aa78ad2287eea007d7a9a9a8aa8d1a71d631427393dd898a0f4a5a6bb05524f00726b620c9d49b75c81fdd515e55f17aec34fa65a83ca87948fa9007f235e9b2c865959ff00bc6bc33c69ab8d6bc4b77346dba18cf9319f555e33f89c9afc4f8fb1d1a596ca95f5ab2497a27ccdfe097ccfb5c868396254ba457e7a189451457f381fa305145140056a785f473aff0088ac2c304a4b203263b20e5bf406b2ebd57e09681ff1f9accabd7fd1e1cfe6e7f90fc0d7d470d656f38cd68e15abc6f797f856afefdbd5a3cbccb15f53c2ceaf5b597abdbfccf57450aa00181d80a751457f699f8c051451401e57f1a7c366486df5b8532d1621b8c7f749f95bf0271f88af251cd7d4ba8d8c3a9d94f6b709e6413218dd4f70457cd5e21d126f0deb373a7cf92d137caffdf43f75bf11fae6bf9a3c47c8de1318b33a2bdcaba4bca697fedcb5f54cfd2b8731deda8bc2cdfbd1dbd3fe07f919f451457e367d8851451401abe17f114fe16d6a1bf806f0bf2cb1671e621eabfd47b815f4568bacda6bda7c57965289a090707b83dc11d88f4af982b53c3de27d47c2d7667b09f606c799130cc727d47f5eb5fa5708717cf87e4f0f884e5424efa6f17dd77bf55f35e7f359be50b304aa53769afb9aecff00467d354579ce85f1a34cbb554d4a1974f97bba83247f98e47e55d85878af47d4941b6d4ed26cff00089541fc8f35fd2380e20caf338a961711195fa5ecff00f01767f81f9c57c062b0ced569b5f97dfb1ad453165571952187b1cd3b35efa69ea8e0168a4cd19a602d1499a33400b452668cd002d1499a33400b452668cfb5002d14d691547240fa9aa773acd9da2932dc46b8f5603f9d653ab0a4af3925ea546329691572ee6b3751bb041890ff00bc7fa573dabfc49d1ecd594ea119ff0062dcf98e7f2e0579febdf14ee2e91a1d2e1368878f3e5c193f01d07eb5f099cf1865580838fb6527da3abf4d36f9d8f77079462abc93e4b2f3d17f5e86f7c42f18269168fa75a3e6fe65dac54ffa943d49ff0068f6fcebc9946053999a4767762eec72ccc7249f52692bf99b3cceab6798af6f515a2b48c7b2fd5beaff00448fd2f0382860a9724757d5f70a28a2be74f4428a28271401359594da95ec1696ebbe79dc468bee6be97d03478741d1ed6c20ff0057046133fde3dcfe2726bcd3e0cf854bbc9aedc2703315a823af667ffd947e35eb638afe99f0e723782c1cb32acbdfabf0f943ff00b67afa24cfcd388b1dedeb2c341e90dfd7fe07f98b451457ec47c78514514005709f14fc1a7c43a50bcb58f76a166a4a81d648fa95faf71f8fad77748466bcbcd32ea19b60ea60b10bdd9afb9f46bcd3d51d585c44f095a35a9eebfab1f2883914b5e83f157c0e746bc6d5eca3c58cedfbe451c4521eff00eeb7e87ea2bcf81cd7f18e6f9562326c64f07895ef4767d1ae8d793ff81ba3f65c262a9e328c6b53d9fe0fb0514515e31d81451450018a4280f500fd452e69635699b6c6ad237a202c7f4a695dd90b6d415993eeb32ffba714ef3e5ff9ed2ffdfc3fe35762f0f6ab38cc7a5deb8f6b77ff000a93fe115d6ffe80f7ff00f80edfe15df1c1e31abc694ade8ffc8c1d6a2b792fbd19de7cbff3da5ffbf87fc6aee8934a75bd3b32c847da63eae7fbc3dea4ff0084575bff00a03dff00fe03b7f855cd1fc31acc7ac583be937a88b711b33340c00018649e2bb30b83c72af4dba53dd747dfd0c6ad6a1c92f796cfaa3dbb272793d4f7a327d4fe74ff00225c9fdd3f5f4347912ffcf27fc8d7f5ab8cbb1f94732ee3327d4fe7464fa9fce9fe44bff3c9ff002347912ffcf27fc8d1cb2ec1ccbb8cc9f53f9d723f145d93c2ac55d94fda23e4311eb5d8f912ff00cf27fc8d72bf1274cbcbef0cb456f693cf2f9f19d91c658e0672702bc1cfe9d49655898c22dbe497e477e025158aa6dbea8f1bf3e5ff009ed2ff00dfc3fe3479d2ff00cf697fefb3fe35a3ff0008aeb7ff00407bff00fc076ff0a3fe115d6ffe80f7dff80eff00e15fcbff0052c77fcfa9ff00e02ffc8fd37db50fe65f7a33bcd93fe7a3ff00df46a365ddd79faf35a4fe1bd5d07cda55eafd606ff0aad369f796dfeb6d2e221eaf130fe95854c36260bf79092f54cb8d5a6fe192fbcafb40a29030e9de96b8cd828a28a0028a28a002b5fc2be1b9bc57ad436316563fbf34a3fe59a0ea7ebd87b9acb82de5bcb88ede08da59a560891a8c9627a0afa13c05e0e8fc23a4089b6bdecd87b894773d947b0ff13debef384386e7c418d5ed17ee61acdf7ed15e6ff0577d8f0737cc560287bbf1cb6ff3f97e66fd85943a759c36d6f188a08502220ec00ab14515fd7b08c611508ab25b1f91b6e4eef70a28a2a8414514500145145004177690df5b4b6f3c6b2c32a94746190c0f515f3ff8f3c133783f50ca06974d98fee263ce3fd86f71fa8fc6be87aa7aae956bacd84d6779109ade518643fcc7a11eb5f15c53c334788b0bcbf0d58fc32fd1f93fc375d9fb595e653cbaadf783dd7eabccf9768ae93c6be07bbf075ef3ba7d3e43886e71ff008eb7a37f3ed5cdd7f23e370588cbb112c2e2a0e338ee9ff5aaecf667eb546bd3c45355693ba604e2b7fc29e08d4bc5f31fb32886d14e1eea51f203e83fbc7d87e38a67833c31278b75c8ecc1296e83ccb8907f0a0f4f73d07ff5abe89b0b0834db386d6da2582de25da91a8e00afd1783783bfb79bc6631b5422ed65bc9f557e89757f25addaf9dce738fa87ee68eb37f87fc1393d0fe13685a4aab4f09d4a71d64b9e573ec838fcf35d75b5941669b2de18e04feec48147e953d15fd2781cab0396c3930746305e4b5f9bddfcd9f9bd7c557c4be6ad372f5131ee68c52d15ea9ca2628c52d140098a314b45002628c52d140098a314b45002628c52d14008573504b6714a395c1f5538ab1454ca3192b490d36b6398d63c21637c8df68b282e97d5a301c7e239ae035ff8551b234ba44c51c73f679db2a7e8ddbf1fcebd948acad46d4467cc41853d40ec6be2b39e19cbb3183955a4afdd6925e69ad7e4ee8f6b0599e230f24a33ff002fb8f9aee6da6b2b892dee2368668ce191c608351d7b178efc269e20d3da78100d4205cc6c3ac8a3aa1fe9eff5af1c0735fcc59fe475723c57b293e684b58beebb3f35d7e4fa9fa6e031d1c752e75a35ba168e49000249e3028e5982a82589c0006493e95ec7f0dbe1a7f6518f55d5a306f7ef436edc887fda6ff6bf97d7a4e4390e2f88314b0f87568af8a5d22bfcfb2ebe9769e3f1f4b2fa5ed2a6fd1757fd75658f863f0f8e8308d4f508ff00e2652afc91b7fcb053dbfde3dfd3a7ad7a10e280314b5fd7f9565586c9b090c1e155a2bef6fab7e6ff00e02d0fc87158aa98caaeb557abfc3c90514515eb9c81451450014514500145145001451450056bfd3edf53b496d6ea149ede51b5e3719045787f8e7e1a5d7865a4bbb20f77a5e724f5787d9bd47fb5f9d7bcd359430208c83d8d7c8f10f0d60b88a8f2575cb517c335baff0035e5f759ea7ad97e655b2f9de1ac5eeba3ff0027e6707f077441a7f863edacbfbebe73267fd81c28fe67f1aefaa2b7b78ad2148a18d62890615106028f402a5af5f28cbe1956028e0a1f62297abeafe6eece4c5e21e2abceb4bed3ff0086fb90514515eb9c814514500145145001451450014514500145145001451450014514500151cf18963653d1862a4a43d29349ab30d8e7b04647420d78cf8c3c393c5e3196d2c6dde66bb2268628c64fcdd7f0041fa57b9496324b7726d1b5339dc6ae5be9d05bc9e6ac6a66dbb0ca47cc5739c67d33dabf3bceb865710528d0a92e45195ef6d6db34bd7eedb73e8b0599fd424e7157badbf238bf017c3287c39b2faff65cea7d540e520ff77d4fbfe55de818a296bec32ccaf099461a385c1c3962bef6fbb7d5ff005b1e3e2715571751d5aceeff00ad828a28af54e50a28a2800a28a2800a28a2800a28a2800a28a2800a28a2800a28a2800a28a2800a28a2800a28a2800a28a2800a28a2800a28a2800a28a2800a28a2800a28a28012968a2800a28a2800a28a2800a28a2800a28a2800a28a2800a28a2800a28a2800a28a2800a28a2800a28a2800a28a2800a28a2800a28a2800a28a2800a28a2800a28a2800a28a2800a28a2800a28a2800a28a2800a28a2800a28a2800a28a2803ffd9');


--
-- Data for Name: nlu_training_sentence; Type: TABLE DATA; Schema: public; Owner: chatbot
--

INSERT INTO public.nlu_training_sentence VALUES (1167, 'Quel temps fait-il sur mars ?', 1107);
INSERT INTO public.nlu_training_sentence VALUES (1168, 'Quel temps fait il sur mars', 1107);
INSERT INTO public.nlu_training_sentence VALUES (1169, 'C''est quoi la météo', 1107);
INSERT INTO public.nlu_training_sentence VALUES (1170, 'peux tu me donner la météo', 1107);
INSERT INTO public.nlu_training_sentence VALUES (1140, 'Quel est la réponse à La Grande Question sur la vie, l''univers et le reste', 1100);
INSERT INTO public.nlu_training_sentence VALUES (1141, 'Quel est le sens de la vie ?', 1100);
INSERT INTO public.nlu_training_sentence VALUES (1142, 'Quel âge as-tu ?', 1101);
INSERT INTO public.nlu_training_sentence VALUES (1143, 'Tu as quel age', 1101);
INSERT INTO public.nlu_training_sentence VALUES (1144, 't''est vieux ?', 1101);
INSERT INTO public.nlu_training_sentence VALUES (1145, 'Tu peux me dire un truc drôle ?', 1102);
INSERT INTO public.nlu_training_sentence VALUES (1146, 'Raconte-moi une blague', 1102);
INSERT INTO public.nlu_training_sentence VALUES (1147, 'Fais moi rire', 1102);
INSERT INTO public.nlu_training_sentence VALUES (1148, 'Fais-moi rire', 1102);
INSERT INTO public.nlu_training_sentence VALUES (1149, 'Fait moi rire', 1102);
INSERT INTO public.nlu_training_sentence VALUES (1150, 'Je suis triste', 1102);
INSERT INTO public.nlu_training_sentence VALUES (1151, 'bonjour', 1103);
INSERT INTO public.nlu_training_sentence VALUES (1152, 'hello', 1103);
INSERT INTO public.nlu_training_sentence VALUES (1153, 'salut', 1103);
INSERT INTO public.nlu_training_sentence VALUES (1154, 'yop', 1103);
INSERT INTO public.nlu_training_sentence VALUES (1155, 'slt', 1103);
INSERT INTO public.nlu_training_sentence VALUES (1156, 'comment ca va ?', 1104);
INSERT INTO public.nlu_training_sentence VALUES (1157, 'Tu va bien', 1104);
INSERT INTO public.nlu_training_sentence VALUES (1158, 'Tu vas bien', 1104);
INSERT INTO public.nlu_training_sentence VALUES (1159, 'Ou habites-tu ?', 1105);
INSERT INTO public.nlu_training_sentence VALUES (1160, 'Quelle ville habites-tu ?', 1105);
INSERT INTO public.nlu_training_sentence VALUES (1161, 'Es-tu un homme ?', 1106);
INSERT INTO public.nlu_training_sentence VALUES (1162, 'Es-tu une femme ?', 1106);
INSERT INTO public.nlu_training_sentence VALUES (1163, 'Es-tu un robot ?', 1106);
INSERT INTO public.nlu_training_sentence VALUES (1164, 'Es-tu réel ?', 1106);
INSERT INTO public.nlu_training_sentence VALUES (1165, 'Tu es humain ?', 1106);
INSERT INTO public.nlu_training_sentence VALUES (1166, 'Tu est humain', 1106);
INSERT INTO public.nlu_training_sentence VALUES (1171, 'Comment t''appelles-tu ?', 1108);
INSERT INTO public.nlu_training_sentence VALUES (1172, 'Qui es-tu ?', 1108);
INSERT INTO public.nlu_training_sentence VALUES (1173, 'Quel est-ton nom ?', 1108);
INSERT INTO public.nlu_training_sentence VALUES (1280, 'tu t''appelle comment ?', 1108);
INSERT INTO public.nlu_training_sentence VALUES (1305, 'Dans la DEM, comment passer à l’étape Estimation du marché ?', 1221);
INSERT INTO public.nlu_training_sentence VALUES (1306, 'Comment on valide la FEB', 1221);
INSERT INTO public.nlu_training_sentence VALUES (1307, 'je veux valider la FEB', 1221);
INSERT INTO public.nlu_training_sentence VALUES (1308, 'je veux valider la Fiche d’expression de besoin', 1221);
INSERT INTO public.nlu_training_sentence VALUES (1309, 'comment on fait l''estimation du marché ?', 1221);
INSERT INTO public.nlu_training_sentence VALUES (1310, 'Dans la DEM, quel acteur complète la démarche et stratégie ?', 1222);
INSERT INTO public.nlu_training_sentence VALUES (1311, 'qui renseigne l’onglet Démarche et stratégie ?', 1222);
INSERT INTO public.nlu_training_sentence VALUES (1312, 'Dans la demande d''achat, qui complète la démarche et stratégie ?', 1222);
INSERT INTO public.nlu_training_sentence VALUES (1300, 'A quel endroit déposer la FEB ?', 1220);
INSERT INTO public.nlu_training_sentence VALUES (1301, 'Où déposer la FEB ?', 1220);
INSERT INTO public.nlu_training_sentence VALUES (1302, 'Comment faire le dépôt FEB', 1220);
INSERT INTO public.nlu_training_sentence VALUES (1303, 'je veux faire le dépot de la FEB', 1220);
INSERT INTO public.nlu_training_sentence VALUES (1304, 'Déposer une Fiche d’expression de besoin', 1220);
INSERT INTO public.nlu_training_sentence VALUES (1340, 'FEB DEM', 1220);
INSERT INTO public.nlu_training_sentence VALUES (1341, 'FEB dans la DEM', 1220);
INSERT INTO public.nlu_training_sentence VALUES (1313, 'Quelle différence entre la demande d’achat et la demande d’achat (simplifiée) ?', 1223);
INSERT INTO public.nlu_training_sentence VALUES (1314, 'DEM ou DEM simplifiée ?', 1223);
INSERT INTO public.nlu_training_sentence VALUES (1315, 'demande ou demande simplifiée ?', 1223);
INSERT INTO public.nlu_training_sentence VALUES (1316, 'Dans quel cas réaliser une DEM simplifiée ?', 1223);
INSERT INTO public.nlu_training_sentence VALUES (1317, 'Dans quel cas réaliser une demande d''achat simplifiée ?', 1223);
INSERT INTO public.nlu_training_sentence VALUES (1327, 'Comment reporter des critères d’un lot à un autre ?', 1226);
INSERT INTO public.nlu_training_sentence VALUES (1328, 'comment dupliquer les critères d’analyse des offres et candidature', 1226);
INSERT INTO public.nlu_training_sentence VALUES (1329, 'ne pas ressaisir critères candidature et offre', 1226);
INSERT INTO public.nlu_training_sentence VALUES (1330, 'est ce que c''est possible de recopier les critères de candidature ?', 1226);
INSERT INTO public.nlu_training_sentence VALUES (1331, 'est ce que c''est possible de recopier les critères d''analyse ?', 1226);
INSERT INTO public.nlu_training_sentence VALUES (1318, 'A quel endroit faut-il renseigner les critères liées à l’analyse des candidatures et des offres ?', 1224);
INSERT INTO public.nlu_training_sentence VALUES (1319, 'où saisir les critères des candidatures et des offres ?', 1224);
INSERT INTO public.nlu_training_sentence VALUES (1320, 'où saisir les critères DAF ?', 1224);
INSERT INTO public.nlu_training_sentence VALUES (1321, 'où saisir les critères procédure', 1224);
INSERT INTO public.nlu_training_sentence VALUES (1322, 'comment ajouter les candidatures', 1224);
INSERT INTO public.nlu_training_sentence VALUES (1323, 'J’ai oublié de renseigner les critères d’analyse des candidature et des offres. Comment faire ?', 1225);
INSERT INTO public.nlu_training_sentence VALUES (1324, 'j''ai oublié un critère', 1225);
INSERT INTO public.nlu_training_sentence VALUES (1325, 'Pas de critère candidature et offre', 1225);
INSERT INTO public.nlu_training_sentence VALUES (1326, 'comment je renseigne les critères', 1225);


--
-- Data for Name: person; Type: TABLE DATA; Schema: public; Owner: chatbot
--

INSERT INTO public.person VALUES (1, 'Eliza', 'Eliza', NULL, 'HgcKx6gG0cqBHeAxDwNSRfAMgTnY5xsgdGskXb93kAM_O7eOvEh-w=');


--
-- Name: seq_chatbot; Type: SEQUENCE SET; Schema: public; Owner: chatbot
--

SELECT pg_catalog.setval('public.seq_chatbot', 1119, true);


--
-- Name: seq_chatbot_node; Type: SEQUENCE SET; Schema: public; Owner: chatbot
--

SELECT pg_catalog.setval('public.seq_chatbot_node', 1144, true);


--
-- Name: seq_groups; Type: SEQUENCE SET; Schema: public; Owner: chatbot
--

SELECT pg_catalog.setval('public.seq_groups', 1000, false);


--
-- Name: seq_media_file_info; Type: SEQUENCE SET; Schema: public; Owner: chatbot
--

SELECT pg_catalog.setval('public.seq_media_file_info', 1739, true);


--
-- Name: seq_nlu_training_sentence; Type: SEQUENCE SET; Schema: public; Owner: chatbot
--

SELECT pg_catalog.setval('public.seq_nlu_training_sentence', 1359, true);


--
-- Name: seq_person; Type: SEQUENCE SET; Schema: public; Owner: chatbot
--

SELECT pg_catalog.setval('public.seq_person', 1000, false);


--
-- Name: seq_small_talk; Type: SEQUENCE SET; Schema: public; Owner: chatbot
--

SELECT pg_catalog.setval('public.seq_small_talk', 1239, true);


--
-- Name: seq_training; Type: SEQUENCE SET; Schema: public; Owner: chatbot
--

SELECT pg_catalog.setval('public.seq_training', 1719, true);


--
-- Name: seq_utter_text; Type: SEQUENCE SET; Schema: public; Owner: chatbot
--

SELECT pg_catalog.setval('public.seq_utter_text', 1359, true);


--
-- Data for Name: small_talk; Type: TABLE DATA; Schema: public; Owner: chatbot
--

INSERT INTO public.small_talk VALUES (1100, '42', NULL, true, 1019);
INSERT INTO public.small_talk VALUES (1101, 'age', NULL, true, 1019);
INSERT INTO public.small_talk VALUES (1102, 'blague', NULL, true, 1019);
INSERT INTO public.small_talk VALUES (1103, 'bonjour', NULL, true, 1019);
INSERT INTO public.small_talk VALUES (1104, 'humeur', NULL, true, 1019);
INSERT INTO public.small_talk VALUES (1105, 'lieux résidence', NULL, true, 1019);
INSERT INTO public.small_talk VALUES (1106, 'question robot', NULL, true, 1019);
INSERT INTO public.small_talk VALUES (1107, 'météo', NULL, true, 1019);
INSERT INTO public.small_talk VALUES (1108, 'nom', NULL, true, 1019);
INSERT INTO public.small_talk VALUES (1221, 'Dépôt FEB > étape estimation', NULL, true, 1100);
INSERT INTO public.small_talk VALUES (1222, 'Dépôt FEB > acteur démarche et stratégie', NULL, true, 1100);
INSERT INTO public.small_talk VALUES (1220, 'Dépôt FEB > où déposer', NULL, true, 1100);
INSERT INTO public.small_talk VALUES (1223, 'Dépôt FEB > différence DEM / DEM simplifiée', NULL, true, 1100);
INSERT INTO public.small_talk VALUES (1226, 'Candidature > report critère', NULL, true, 1100);
INSERT INTO public.small_talk VALUES (1224, 'Candidature > ajouter', NULL, true, 1100);
INSERT INTO public.small_talk VALUES (1225, 'Candidature > Oubli critères', NULL, true, 1100);


--
-- Data for Name: training; Type: TABLE DATA; Schema: public; Owner: chatbot
--



--
-- Data for Name: utter_text; Type: TABLE DATA; Schema: public; Owner: chatbot
--

INSERT INTO public.utter_text VALUES (1040, 'Je n''ai pas compris, merci de reformuler.', NULL);
INSERT INTO public.utter_text VALUES (1041, 'Bonjour, je suis Alan, votre assistant virtuel.', NULL);
INSERT INTO public.utter_text VALUES (1202, 'Bonjour, je suis Alan, votre assistant virtuel.', 1103);
INSERT INTO public.utter_text VALUES (1203, 'L''avantage d''être un robot, c''est que je suis toujours au top !', 1104);
INSERT INTO public.utter_text VALUES (1204, '<a href="https://goo.gl/maps/rQUG5Fr4iuk">Ici !</a>', 1105);
INSERT INTO public.utter_text VALUES (1205, 'Je suis un robot, donc un être non binaire conçu de manière binaire (c''est pourtant clair).', 1106);
INSERT INTO public.utter_text VALUES (1180, 'Si j''en crois l''oeuvre de Douglas Adams, ce serait 42', 1100);
INSERT INTO public.utter_text VALUES (1181, 'Facile, c''est 42 !', 1100);
INSERT INTO public.utter_text VALUES (1182, 'Hum... mes calculs aboutissent à 101010', 1100);
INSERT INTO public.utter_text VALUES (1183, 'Certains pourraient dire 2A', 1100);
INSERT INTO public.utter_text VALUES (1184, '1325391984000 millisecondes', 1101);
INSERT INTO public.utter_text VALUES (1185, 'Un monsieur visite un musée.<br>Soudain il s''arrête et dit au guide :<br>- Ah, c''est moche !<br>- C''est du Picasso, répond le guide.<br>- Plus loin, il s''écrie de nouveau :<br>- Ah, c''est vraiment moche !<br>- Ca Monsieur, c''est un miroir !', 1102);
INSERT INTO public.utter_text VALUES (1186, 'Pourquoi un chasseur emmène-t-il son fusil aux toilettes ?<br>Pour tirer la chasse.', 1102);
INSERT INTO public.utter_text VALUES (1187, 'Qu''est-ce qu''une manifestation d''aveugles ?<br>Un festival de Cannes', 1102);
INSERT INTO public.utter_text VALUES (1188, 'Quelle est la différence entre une échelle et un pistolet ?<br>L''échelle sert à monter, le pistolet sert à descendre.', 1102);
INSERT INTO public.utter_text VALUES (1189, 'Savez-vous quel est le sport le plus fruité au monde ?<br>La boxe parce que tu reçois une pêche en pleine poire, tu tombes dans les pommes et tu ne ramènes plus ta fraise.', 1102);
INSERT INTO public.utter_text VALUES (1190, 'Vous connaissez l''histoire de l''armoire ? Elle est pas commode...', 1102);
INSERT INTO public.utter_text VALUES (1191, 'Deux ouvriers vont travailler sur la Tour Eiffel. Soudain, ils s''aperçoivent qu''ils ont oublié leur mètre. A quelle hauteur sont-ils ?<br>Ils sont à 200 mètres (à deux sans mètre) !', 1102);
INSERT INTO public.utter_text VALUES (1192, '- Papa, c''est vrai que tu m''aimes pas ?<br>- Mais non fiston... Tiens, prend ton ballon et va jouer sur l''autoroute.', 1102);
INSERT INTO public.utter_text VALUES (1193, 'L''autre jour, j’ai raconté une blague sur Carrefour, mais elle a pas supermarché...', 1102);
INSERT INTO public.utter_text VALUES (1194, '- Les gens devraient dormir leur fenêtre ouverte...<br>- Pourquoi, vous êtes médecin ?<br>- Non, cambrioleur !', 1102);
INSERT INTO public.utter_text VALUES (1195, 'C''est un gars qui se promène au bord d''un lac, tout à coup il voit quelqu''un qui se débat dans l''eau en criant : " HELP !! HELP  !! ", alors il lui crie :<br>- Eh idiot, t''aurais mieux fait d''apprendre à nager au lieu d''apprendre l''anglais !', 1102);
INSERT INTO public.utter_text VALUES (1196, 'Que fait une vache quand elle a les yeux fermés ?<br>Elle fabrique du lait concentré !', 1102);
INSERT INTO public.utter_text VALUES (1197, '- Docteur, je crois que j''ai besoin de lunettes.<br>- Oui certainement. Ici c''est une banque.', 1102);
INSERT INTO public.utter_text VALUES (1198, 'A la maternité un nouveau père, inquiet, demande à la sage-femme :<br>- Trouvez-vous que mon fils me ressemble ?<br>- Oui, mais c''est pas grave, l''essentiel c''est qu''il soit en bonne santé !', 1102);
INSERT INTO public.utter_text VALUES (1199, 'Deux personnes discutent :<br>- Je crois que j''aurais dû écouter ma mère...<br>- Ah bon ? Elle te disait quoi ?<br>- Bah je sais pas, je l''écoutais pas', 1102);
INSERT INTO public.utter_text VALUES (1200, '- Docteur j''ai mal à l''oeil gauche quand je bois mon café.<br>- Essayez d''enlever la cuillère de la tasse.', 1102);
INSERT INTO public.utter_text VALUES (1201, 'Un touriste visite un château en Écosse. Il s''adresse au châtelain :<br>- On m''a dit que votre château était hanté !<br>- Hanté mon château ? ça fait 300 ans que je vis ici, j''ai jamais vu aucun fantôme !', 1102);
INSERT INTO public.utter_text VALUES (1206, 'Il fait -27° au Mont Olympus, mais à l''intérieur de la base, il fait 22°, comme partout ailleurs.', 1107);
INSERT INTO public.utter_text VALUES (1207, 'Alan', 1108);
INSERT INTO public.utter_text VALUES (1320, 'Désolé, je n''ai pas compris la question.', NULL);
INSERT INTO public.utter_text VALUES (1321, 'Bonjour, que puis-je pour vous ?', NULL);
INSERT INTO public.utter_text VALUES (1341, 'Il est nécessaire d’avoir déposé la FEB pour compléter à l’onglet « Estimation du marché ».', 1221);
INSERT INTO public.utter_text VALUES (1340, 'La FEB est à déposer dans votre demande d’achat (DEM), dans l’onglet « Fiche d’expression de besoin », en cliquant sur l’action « Déposer la FEB ».', 1220);
INSERT INTO public.utter_text VALUES (1342, 'Les informations de l’onglet « Démarche et Stratégie » de la DEM peuvent être complétées de façon facultative par le prescripteur, s’il en a connaissance.[pause]Les informations seront complétées et/ou modifiées par l’acheteur lorsque cette DEM sera transformée en dossier d’affaire (DAF).', 1222);
INSERT INTO public.utter_text VALUES (1343, 'La demande d’achat contient 5 étapes matérialisées par 5 onglets : Description, Fiche d’expression de besoin (FEB), Estimation du marché, Démarche d’achat et Traitement Achat, alors que la DEM simplifiée n’en contient que 3.[pause]Elle s’affranchit des étapes liées à la FEB et la Démarche d’achat.[pause]Il est vivement de réaliser une Demande d’achat (DEM) et de ne réaliser une DEM simplifiée qu’en cas de demande d’achat urgente validée de façon express par les valideurs hiérarchiques.', 1223);
INSERT INTO public.utter_text VALUES (1346, 'Si la totalité ou la plupart des critères sont communs à deux lots ou plus, vous pouvez utiliser la fonctionnalité « Préparation des données des lots » disponible à l’étape/onglet « Elaboration FDC/DCE » de votre dossier d’affaire.[pause]Vous devez pour cela vous rendre sur le sous-onglet « Critères applicables aux candidatures » et/ou « Critères applicables aux offres » puis cliquer sur l’icône « engrenage » pour « Lancer l’insertion » de ces critères dans le ou les lots sélectionnés.', 1226);
INSERT INTO public.utter_text VALUES (1344, 'Les critères d’analyse des candidatures et des offres sont à renseigner dans le dossier d’affaire (DAF), à l’étape/onglet « Elaboration FDC/DCE », dans le sous-onglet « Lots », car les critères se renseignent dans chaque lot de la consultation (DAF).[pause]Vous avez deux possibilités :<br>- Soit les renseigner via la fonctionnalité « Préparation des données des lots » dans le cas où les critères sont communs à tous les lots<br>- Soit dans chacun des lots en sélectionnant les lots les uns après les autres et en vous rendant dans les sous-onglets « Critères applicables aux candidatures » et « Critères applicables aux offres ».', 1224);
INSERT INTO public.utter_text VALUES (1345, 'Si votre dossier d’affaire en est à l’étape « Elaboration FDC/DCE », vous  avez deux possibilités :<br>- Soit les renseigner via la fonctionnalité « Préparation des données des lots » dans le cas où les critères sont communs à tous les lots<br>- Soit dans chacun des lots en sélectionnant les lots les uns après les autres et en vous rendant dans les sous-onglets « Critères applicables aux candidatures » et « Critères applicables aux offres ».[pause]En revanche, si vous avez dépassé cette étape « Elaboration FDC/DCE », vous ne pouvez plus les renseigner.[pause]Il convient alors d’analyser les candidatures et les offres sur un document annexe, renseigné manuellement, que vous joindrez dans le service « Pièce-jointe » de votre dossier d’affaire.', 1225);


--
-- Name: chatbot pk_chatbot; Type: CONSTRAINT; Schema: public; Owner: chatbot
--

ALTER TABLE ONLY public.chatbot
    ADD CONSTRAINT pk_chatbot PRIMARY KEY (bot_id);


--
-- Name: groups pk_groups; Type: CONSTRAINT; Schema: public; Owner: chatbot
--

ALTER TABLE ONLY public.groups
    ADD CONSTRAINT pk_groups PRIMARY KEY (grp_id);


--
-- Name: media_file_info pk_media_file_info; Type: CONSTRAINT; Schema: public; Owner: chatbot
--

ALTER TABLE ONLY public.media_file_info
    ADD CONSTRAINT pk_media_file_info PRIMARY KEY (fil_id);


--
-- Name: nlu_training_sentence pk_nlu_training_sentence; Type: CONSTRAINT; Schema: public; Owner: chatbot
--

ALTER TABLE ONLY public.nlu_training_sentence
    ADD CONSTRAINT pk_nlu_training_sentence PRIMARY KEY (nts_id);


--
-- Name: chatbot_node pk_node; Type: CONSTRAINT; Schema: public; Owner: chatbot
--

ALTER TABLE ONLY public.chatbot_node
    ADD CONSTRAINT pk_node PRIMARY KEY (nod_id);


--
-- Name: person pk_person; Type: CONSTRAINT; Schema: public; Owner: chatbot
--

ALTER TABLE ONLY public.person
    ADD CONSTRAINT pk_person PRIMARY KEY (per_id);


--
-- Name: small_talk pk_small_talk; Type: CONSTRAINT; Schema: public; Owner: chatbot
--

ALTER TABLE ONLY public.small_talk
    ADD CONSTRAINT pk_small_talk PRIMARY KEY (smt_id);


--
-- Name: training pk_training; Type: CONSTRAINT; Schema: public; Owner: chatbot
--

ALTER TABLE ONLY public.training
    ADD CONSTRAINT pk_training PRIMARY KEY (tra_id);


--
-- Name: utter_text pk_utter_text; Type: CONSTRAINT; Schema: public; Owner: chatbot
--

ALTER TABLE ONLY public.utter_text
    ADD CONSTRAINT pk_utter_text PRIMARY KEY (utt_id);


--
-- Name: chatbot_media_file_info_media_file_info_fk; Type: INDEX; Schema: public; Owner: chatbot
--

CREATE INDEX chatbot_media_file_info_media_file_info_fk ON public.chatbot USING btree (fil_id_avatar);


--
-- Name: chatbot_name_idx; Type: INDEX; Schema: public; Owner: chatbot
--

CREATE UNIQUE INDEX chatbot_name_idx ON public.chatbot USING btree (lower((name)::text));


--
-- Name: chatbot_utter_text_default_utter_text_fk; Type: INDEX; Schema: public; Owner: chatbot
--

CREATE INDEX chatbot_utter_text_default_utter_text_fk ON public.chatbot USING btree (utt_id_default);


--
-- Name: chatbot_utter_text_welcome_utter_text_fk; Type: INDEX; Schema: public; Owner: chatbot
--

CREATE INDEX chatbot_utter_text_welcome_utter_text_fk ON public.chatbot USING btree (utt_id_welcome);


--
-- Name: node_chatbot_chatbot_fk; Type: INDEX; Schema: public; Owner: chatbot
--

CREATE INDEX node_chatbot_chatbot_fk ON public.chatbot_node USING btree (bot_id);


--
-- Name: node_training_training_fk; Type: INDEX; Schema: public; Owner: chatbot
--

CREATE INDEX node_training_training_fk ON public.chatbot_node USING btree (tra_id);


--
-- Name: person_groups_groups_fk; Type: INDEX; Schema: public; Owner: chatbot
--

CREATE INDEX person_groups_groups_fk ON public.person USING btree (grp_id);


--
-- Name: small_talk_chatbot_chatbot_fk; Type: INDEX; Schema: public; Owner: chatbot
--

CREATE INDEX small_talk_chatbot_chatbot_fk ON public.small_talk USING btree (bot_id);


--
-- Name: small_talk_nlu_training_sentence_small_talk_fk; Type: INDEX; Schema: public; Owner: chatbot
--

CREATE INDEX small_talk_nlu_training_sentence_small_talk_fk ON public.nlu_training_sentence USING btree (smt_id);


--
-- Name: small_talk_utter_text_small_talk_fk; Type: INDEX; Schema: public; Owner: chatbot
--

CREATE INDEX small_talk_utter_text_small_talk_fk ON public.utter_text USING btree (smt_id);


--
-- Name: training_chatbot_chatbot_fk; Type: INDEX; Schema: public; Owner: chatbot
--

CREATE INDEX training_chatbot_chatbot_fk ON public.training USING btree (bot_id);


--
-- Name: training_media_file_info_media_file_info_fk; Type: INDEX; Schema: public; Owner: chatbot
--

CREATE INDEX training_media_file_info_media_file_info_fk ON public.training USING btree (fil_id_model);


--
-- Name: chatbot fk_chatbot_media_file_info_media_file_info; Type: FK CONSTRAINT; Schema: public; Owner: chatbot
--

ALTER TABLE ONLY public.chatbot
    ADD CONSTRAINT fk_chatbot_media_file_info_media_file_info FOREIGN KEY (fil_id_avatar) REFERENCES public.media_file_info(fil_id);


--
-- Name: chatbot fk_chatbot_utter_text_default_utter_text; Type: FK CONSTRAINT; Schema: public; Owner: chatbot
--

ALTER TABLE ONLY public.chatbot
    ADD CONSTRAINT fk_chatbot_utter_text_default_utter_text FOREIGN KEY (utt_id_default) REFERENCES public.utter_text(utt_id);


--
-- Name: chatbot fk_chatbot_utter_text_welcome_utter_text; Type: FK CONSTRAINT; Schema: public; Owner: chatbot
--

ALTER TABLE ONLY public.chatbot
    ADD CONSTRAINT fk_chatbot_utter_text_welcome_utter_text FOREIGN KEY (utt_id_welcome) REFERENCES public.utter_text(utt_id);


--
-- Name: chatbot_node fk_node_chatbot_chatbot; Type: FK CONSTRAINT; Schema: public; Owner: chatbot
--

ALTER TABLE ONLY public.chatbot_node
    ADD CONSTRAINT fk_node_chatbot_chatbot FOREIGN KEY (bot_id) REFERENCES public.chatbot(bot_id);


--
-- Name: chatbot_node fk_node_training_training; Type: FK CONSTRAINT; Schema: public; Owner: chatbot
--

ALTER TABLE ONLY public.chatbot_node
    ADD CONSTRAINT fk_node_training_training FOREIGN KEY (tra_id) REFERENCES public.training(tra_id);


--
-- Name: person fk_person_groups_groups; Type: FK CONSTRAINT; Schema: public; Owner: chatbot
--

ALTER TABLE ONLY public.person
    ADD CONSTRAINT fk_person_groups_groups FOREIGN KEY (grp_id) REFERENCES public.groups(grp_id);


--
-- Name: small_talk fk_small_talk_chatbot_chatbot; Type: FK CONSTRAINT; Schema: public; Owner: chatbot
--

ALTER TABLE ONLY public.small_talk
    ADD CONSTRAINT fk_small_talk_chatbot_chatbot FOREIGN KEY (bot_id) REFERENCES public.chatbot(bot_id);


--
-- Name: nlu_training_sentence fk_small_talk_nlu_training_sentence_small_talk; Type: FK CONSTRAINT; Schema: public; Owner: chatbot
--

ALTER TABLE ONLY public.nlu_training_sentence
    ADD CONSTRAINT fk_small_talk_nlu_training_sentence_small_talk FOREIGN KEY (smt_id) REFERENCES public.small_talk(smt_id);


--
-- Name: utter_text fk_small_talk_utter_text_small_talk; Type: FK CONSTRAINT; Schema: public; Owner: chatbot
--

ALTER TABLE ONLY public.utter_text
    ADD CONSTRAINT fk_small_talk_utter_text_small_talk FOREIGN KEY (smt_id) REFERENCES public.small_talk(smt_id);


--
-- Name: training fk_training_chatbot_chatbot; Type: FK CONSTRAINT; Schema: public; Owner: chatbot
--

ALTER TABLE ONLY public.training
    ADD CONSTRAINT fk_training_chatbot_chatbot FOREIGN KEY (bot_id) REFERENCES public.chatbot(bot_id);


--
-- Name: training fk_training_media_file_info_media_file_info; Type: FK CONSTRAINT; Schema: public; Owner: chatbot
--

ALTER TABLE ONLY public.training
    ADD CONSTRAINT fk_training_media_file_info_media_file_info FOREIGN KEY (fil_id_model) REFERENCES public.media_file_info(fil_id);


--
-- PostgreSQL database dump complete
--

